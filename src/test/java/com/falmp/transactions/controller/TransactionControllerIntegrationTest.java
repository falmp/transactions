package com.falmp.transactions.controller;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@IfProfileValue(name = ACTIVE_PROFILES_PROPERTY_NAME, value = "it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TransactionControllerIntegrationTest {
    @Autowired
    private TestRestTemplate template;

    @LocalServerPort
    private Integer port;

    @Value("${app.metrics.window}")
    private Long metricsWindow;

    private String base;

    @Before
    public void setUp() throws Exception {
        base = "http://localhost:" + port;
    }

    @Test
    public void testGetStatistics() throws Exception {
        ResponseEntity<Metrics> response = template.getForEntity(base + "/statistics", Metrics.class);
        assertThat(OK).isEqualTo(response.getStatusCode());
    }

    @Test
    public void testPostTransactionsOk() throws Exception {
        Long now = System.currentTimeMillis();
        Transaction transaction = new Transaction(1.0, now);

        ResponseEntity<String> response = template.postForEntity(base + "/transactions", transaction, String.class);
        assertThat(OK).isEqualTo(response.getStatusCode());
    }

    @Test
    public void testPostTransactionsNoContent() throws Exception {
        Long past = System.currentTimeMillis() - 1;
        Transaction transaction = new Transaction(1.0, past - metricsWindow);

        ResponseEntity<String> response = template.postForEntity(base + "/transactions", transaction, String.class);
        assertThat(NO_CONTENT).isEqualTo(response.getStatusCode());
    }
}
