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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@RunWith(SpringRunner.class)
public class TransactionControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void testGetStatistics() throws Exception {
        Metrics metrics = new Metrics(1.0);
        when(transactionService.getMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("avg").value(1.0))
                .andExpect(jsonPath("sum").value(1.0))
                .andExpect(jsonPath("min").value(1.0))
                .andExpect(jsonPath("max").value(1.0))
                .andExpect(jsonPath("count").value(1L));
    }

    @Test
    public void testPostTransactionsOk() throws Exception {
        Long now = System.currentTimeMillis();
        Transaction transaction = new Transaction(1.0, now);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(true);

        mockMvc.perform(
                post("/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction))
        ).andExpect(status().isOk());
    }

    @Test
    public void testPostTransactionsNoContent() throws Exception {
        Long past = System.currentTimeMillis() - 1;
        Transaction transaction = new Transaction(1.0, past - metricsWindow);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(false);

        mockMvc.perform(
                post("/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction))
        ).andExpect(status().isNoContent());
    }
}