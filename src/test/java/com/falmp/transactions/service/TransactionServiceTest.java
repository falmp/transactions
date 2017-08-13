package com.falmp.transactions.service;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import com.falmp.transactions.repository.TransactionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {
    @MockBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @Test
    public void testCreateTransactionAccepted() throws Exception {
        Long now = System.currentTimeMillis();
        Transaction transaction = new Transaction(1.0, now);

        Boolean result = transactionService.createTransaction(transaction);
        assertThat(result).isTrue();

        verify(transactionRepository).createTransaction(transaction);
    }

    @Test
    public void testCreateTransactionNotAccepted() throws Exception {
        Long past = System.currentTimeMillis() - 1;
        Transaction transaction = new Transaction(1.0, past - metricsWindow);

        Boolean result = transactionService.createTransaction(transaction);
        assertThat(result).isFalse();
    }

    @Test
    public void testGetMetrics() throws Exception {
        Metrics metrics = new Metrics(1.0);
        when(transactionService.getMetrics()).thenReturn(metrics);

        Metrics result = transactionService.getMetrics();
        assertThat(result).isEqualToComparingFieldByField(metrics);
    }
}