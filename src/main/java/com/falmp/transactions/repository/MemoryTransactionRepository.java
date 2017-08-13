package com.falmp.transactions.repository;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BinaryOperator;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@Repository
public class MemoryTransactionRepository implements TransactionRepository {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Metrics emptyMetrics = new Metrics();
    private final AtomicReference<Metrics> metrics = new AtomicReference<>(emptyMetrics);
    private final ConcurrentNavigableMap<Long, Metrics> timestampMetrics = new ConcurrentSkipListMap<>();
    private final ConcurrentNavigableMap<Long, Lock> timestampLocks = new ConcurrentSkipListMap<>();
    private final Lock timestampLocksLock = new ReentrantLock();

    private final BinaryOperator<Metrics> mergeMetrics = (m1, m2) -> {
        Double sum = m1.getSum() + m2.getSum();
        Double max = Math.max(m1.getMax(), m2.getMax());
        Double min = Math.min(m1.getMin(), m2.getMin());
        Long count = m1.getCount() + m2.getCount();

        Double total1 = m1.getAvg() * m1.getCount();
        Double total2 = m2.getAvg() * m2.getCount();
        Double avg = (total1 + total2) / count;

        return new Metrics(sum, avg, max, min, count);
    };

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @Async
    @Override
    public void createTransaction(Transaction transaction) {
        logger.debug("Adding transaction {} asynchronously", transaction);

        Long key = getKey(transaction.getTimestamp());

        // ConcurrentSkipListMap.merge() is not guaranteed to be applied once atomically, but we can lock for the
        // current key only.
        Lock keyLock = getKeyLock(key);
        keyLock.lock();
        try {
            timestampMetrics.merge(key, new Metrics(transaction.getAmount()), mergeMetrics);
        } finally {
            keyLock.unlock();
        }
    }

    private Lock getKeyLock(Long key) {
        Lock keyLock = timestampLocks.get(key);
        if (keyLock != null)
            return keyLock;

        // ConcurrentSkipListMap.computeIfAbsent() is not guaranteed to be applied once atomically.
        timestampLocksLock.lock();
        try {
            return timestampLocks.computeIfAbsent(key, k -> new ReentrantLock());
        } finally {
            timestampLocksLock.unlock();
        }
    }

    @Override
    public Metrics getMetrics() {
        return metrics.get();
    }

    @Scheduled(cron = "${app.metrics.cron-update-rate:* * * * * *}")
    public void aggregateMetrics() {
        clearMetrics();

        Metrics currentMetrics = timestampMetrics.values().stream()
                .reduce(mergeMetrics)
                .orElse(emptyMetrics);
        metrics.set(currentMetrics);

        logger.info("Metrics updated: {}", currentMetrics);
    }

    private void clearMetrics() {
        Long now = System.currentTimeMillis();
        Long key = getKey(now - metricsWindow);

        timestampMetrics.headMap(key).clear();
        timestampLocks.headMap(key).clear();

        logger.debug("Metrics after cleanup: {}", timestampMetrics);
        logger.debug("All metrics older than {} were removed", key);
    }

    private long getKey(Long timestamp) {
        return timestamp / 1000;
    }
}
