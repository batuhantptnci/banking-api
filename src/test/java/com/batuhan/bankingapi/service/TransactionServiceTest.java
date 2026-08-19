package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transactionService = new TransactionService(
                transactionRepository
        );
    }
    @Test
    void shouldCreateTransactionSuccessfully() {

        Account senderAccount = new Account();
        senderAccount.setId(1L);

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(
                TransactionType.TRANSFER,
                new BigDecimal("300.00"),
                senderAccount,
                receiverAccount
        );

        assertEquals(TransactionType.TRANSFER, result.getType());
        assertEquals(new BigDecimal("300.00"), result.getAmount());
        assertEquals(senderAccount, result.getAccount());
        assertEquals(receiverAccount, result.getTargetAccount());

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    @Test
    void shouldCreateDepositTransactionWithoutTargetAccount() {

        Account account = new Account();
        account.setId(1L);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.createTransaction(
                TransactionType.DEPOSIT,
                new BigDecimal("500.00"),
                account,
                null
        );

        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals(account, result.getAccount());
        assertNull(result.getTargetAccount());

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    @Test
    void shouldGetTransactionsByAccountId() {

        Long accountId = 6L;

        Transaction transaction = new Transaction();

        when(transactionRepository
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                ))
                .thenReturn(List.of(transaction));

        List<Transaction> result =
                transactionService.getTransactionsByAccountId(accountId);

        assertEquals(1, result.size());

        verify(transactionRepository)
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                );
    }
    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {

        Long accountId = 6L;

        when(transactionRepository
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                ))
                .thenReturn(List.of());

        List<Transaction> result =
                transactionService.getTransactionsByAccountId(accountId);

        assertTrue(result.isEmpty());

        verify(transactionRepository)
                .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                );
    }

}