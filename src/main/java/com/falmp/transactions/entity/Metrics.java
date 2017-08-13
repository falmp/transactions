package com.falmp.transactions.entity;

public class Metrics {
    private final Double sum;
    private final Double avg;
    private final Double max;
    private final Double min;
    private final Long count;

    public Metrics(Double sum, Double avg, Double max, Double min, Long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public Metrics(Double amount) {
        this(amount, amount, amount, amount, 1L);
    }

    public Metrics() {
        this(0.0, 0.0, 0.0, 0.0, 0L);
    }

    public Double getSum() {
        return sum;
    }

    public Double getAvg() {
        return avg;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "sum=" + sum +
                ", avg=" + avg +
                ", max=" + max +
                ", min=" + min +
                ", count=" + count +
                '}';
    }
}
