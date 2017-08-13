package com.falmp.transactions.controller;

import com.falmp.transactions.entity.Metrics;
import com.falmp.transactions.entity.Transaction;
import com.falmp.transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity postTransactions(@RequestBody Transaction transaction) {
        Boolean accepted = transactionService.createTransaction(transaction);

        return new ResponseEntity(accepted ? OK : NO_CONTENT);
    }

    @GetMapping("/statistics")
    public Metrics getStatistics() {
        return transactionService.getMetrics();
    }
}
