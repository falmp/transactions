package com.falmp.transactions.controller;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import com.falmp.transactions.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionControllerTest {
    @Autowired
    private TransactionController transactionController;

    @MockBean
    private TransactionService transactionService;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @Test
    public void testGetStatistics() throws Exception {
        Metrics metrics = new Metrics(1.0);
        when(transactionService.getMetrics()).thenReturn(metrics);

        Metrics result = transactionController.getStatistics();
        assertThat(result).isEqualToComparingFieldByField(metrics);
    }

    @Test
    public void testPostTransactionsOk() throws Exception {
        Long now = System.currentTimeMillis();
        Transaction transaction = new Transaction(1.0, now);
        when(transactionService.createTransaction(transaction)).thenReturn(true);

        ResponseEntity result = transactionController.postTransactions(transaction);
        assertThat(result.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void testPostTransactionsNoContent() throws Exception {
        Long past = System.currentTimeMillis() - 1;
        Transaction transaction = new Transaction(1.0, past - metricsWindow);
        when(transactionService.createTransaction(transaction)).thenReturn(false);

        ResponseEntity result = transactionController.postTransactions(transaction);
        assertThat(result.getStatusCode()).isEqualTo(NO_CONTENT);
    }
}