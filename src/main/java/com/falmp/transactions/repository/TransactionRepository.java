package com.falmp.transactions.repository;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;

public interface TransactionRepository {
    void createTransaction(Transaction transaction);

    Metrics getMetrics();
}
