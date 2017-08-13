package com.falmp.transactions.service;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import com.falmp.transactions.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    public Boolean createTransaction(Transaction transaction) {
        logger.info("Adding transaction {}", transaction);

        Long now = System.currentTimeMillis();
        if (transaction.getTimestamp() < now - metricsWindow) {
            logger.info("Transaction {} is older than metrics window, discarding it...", transaction);

            return false;
        }

        transactionRepository.createTransaction(transaction);

        return true;
    }

    public Metrics getMetrics() {
        return transactionRepository.getMetrics();
    }
}
