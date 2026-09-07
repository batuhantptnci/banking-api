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

    public TransactionService(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository =
                transactionRepository;
    }

    public Transaction createTransaction(
            TransactionType type,
            BigDecimal amount,
            Account account,
            Account targetAccount
    ) {
        return createTransaction(
                type,
                amount,
                account,
                targetAccount,
                null
        );
    }

    public Transaction createTransaction(
            TransactionType type,
            BigDecimal amount,
            Account account,
            Account targetAccount,
            String description
    ) {
        Transaction transaction =
                new Transaction();

        transaction.setType(type);
        transaction.setAmount(amount);

        transaction.setAccount(account);
        transaction.setTargetAccount(
                targetAccount
        );

        transaction.setSourceBalanceAfter(
                account.getBalance()
        );

        transaction.setTargetBalanceAfter(
                targetAccount != null
                        ? targetAccount.getBalance()
                        : null
        );

        transaction.setDescription(
                normalizeDescription(description)
        );

        return transactionRepository.save(
                transaction
        );
    }

    private String normalizeDescription(
            String description
    ) {
        if (description == null) {
            return null;
        }

        String normalized =
                description.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    public List<Transaction>
    getTransactionsByAccountId(
            Long accountId
    ) {
        return transactionRepository
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                );
    }
}