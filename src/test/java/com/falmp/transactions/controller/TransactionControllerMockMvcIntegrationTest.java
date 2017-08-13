package com.falmp.transactions.controller;

import com.falmp.transactions.entity.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IfProfileValue(name = ACTIVE_PROFILES_PROPERTY_NAME, value = "it")
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class TransactionControllerMockMvcIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    @Test
    public void testGetStatistics() throws Exception {
        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostTransactionsOk() throws Exception {
        Long now = System.currentTimeMillis();
        Transaction transaction = new Transaction(1.0, now);

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

        mockMvc.perform(
                post("/transactions")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction))
        ).andExpect(status().isNoContent());
    }
}
