package com.falmp.transactions.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class MetricsTest {
    @Test
    public void testConstructorForEmptyMetrics() throws Exception {
        Metrics metrics = new Metrics(0.0, 0.0, 0.0, 0.0, 0L);

        Metrics result = new Metrics();
        assertThat(result).isEqualToComparingFieldByField(metrics);
    }

    @Test
    public void testConstructorForEmptyWithSingleTransaction() throws Exception {
        Metrics metrics = new Metrics(1.0, 1.0, 1.0, 1.0, 1L);

        Metrics result = new Metrics(1.0);
        assertThat(result).isEqualToComparingFieldByField(metrics);
    }
}