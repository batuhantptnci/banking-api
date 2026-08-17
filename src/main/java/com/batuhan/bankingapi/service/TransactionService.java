package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;
import com.batuhan.bankingapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(
            TransactionType type,
            BigDecimal amount,
            Account account,
            Account targetAccount
    ) {

        Transaction transaction = new Transaction();

        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setAccount(account);
        transaction.setTargetAccount(targetAccount);

        return transactionRepository.save(transaction);
    }
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                );
    }
}