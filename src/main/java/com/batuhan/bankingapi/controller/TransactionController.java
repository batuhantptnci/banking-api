package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.TransactionResponse;
import com.batuhan.bankingapi.mapper.TransactionMapper;
import com.batuhan.bankingapi.service.TransactionService;
import com.batuhan.bankingapi.dto.AccountTransactionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }
    @GetMapping("/account/{accountId}")
    public List<AccountTransactionResponse> getTransactionsByAccountId(
            @PathVariable Long accountId
    ) {
        return transactionService.getTransactionsByAccountId(accountId)
                .stream()
                .map(transaction ->
                        TransactionMapper.toAccountResponse(
                                transaction,
                                accountId
                        )
                )
                .toList();
    }
}