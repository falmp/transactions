package com.falmp.transactions.repository;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest
@RunWith(SpringRunner.class)
public class MemoryTransactionRepositoryTest {
    private final Long AWAIT_ASYNC_PROCESSING = 200L;

    @Autowired
    private MemoryTransactionRepository transactionRepository;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @Test
    public void testMetricsInitialization() throws Exception {
        assertThat(transactionRepository.getMetrics()).isEqualToComparingFieldByField(new Metrics());
    }

    @Test
    public void testAggregateMetricsWithSingleTransaction() throws Exception {
        Long now = System.currentTimeMillis();

        transactionRepository.createTransaction(new Transaction(1.0, now));
        Thread.sleep(AWAIT_ASYNC_PROCESSING);
        transactionRepository.aggregateMetrics();
        assertThat(transactionRepository.getMetrics()).isEqualToComparingFieldByField(new Metrics(1.0));
    }

    @Test
    public void testAggregateMetricsWithMultipleTransactions() throws Exception {
        Long now = System.currentTimeMillis();

        transactionRepository.createTransaction(new Transaction(1.0, now));
        transactionRepository.createTransaction(new Transaction(2.0, now));
        transactionRepository.createTransaction(new Transaction(3.0, now));
        Thread.sleep(AWAIT_ASYNC_PROCESSING);
        transactionRepository.aggregateMetrics();
        assertThat(transactionRepository.getMetrics()).isEqualToComparingFieldByField(new Metrics(6.0, 2.0, 3.0, 1.0, 3L));
    }

    @Ignore
    @Test
    public void testAggregateMetricsDiscardOldTransactions() throws Exception {
        Long now = System.currentTimeMillis();
        Long future = now + metricsWindow;

        transactionRepository.createTransaction(new Transaction(1.0, now));
        transactionRepository.createTransaction(new Transaction(2.0, future));
        Thread.sleep(AWAIT_ASYNC_PROCESSING);
        transactionRepository.aggregateMetrics();
        assertThat(transactionRepository.getMetrics()).isEqualToComparingFieldByField(new Metrics(3.0, 1.5, 2.0, 1.0, 2L));

        Thread.sleep(metricsWindow);
        transactionRepository.aggregateMetrics();
        assertThat(transactionRepository.getMetrics()).isEqualToComparingFieldByField(new Metrics(2.0));
    }
}