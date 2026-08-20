package com.batuhan.bankingapi.integration;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.InsufficientBalanceException;
import com.batuhan.bankingapi.repository.AccountRepository;
import com.batuhan.bankingapi.repository.TransactionRepository;
import com.batuhan.bankingapi.repository.UserRepository;
import com.batuhan.bankingapi.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AccountConcurrencyIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void shouldPreventDoubleWithdrawWithPessimisticLock() throws Exception {

        String email = "concurrency-" + UUID.randomUUID() + "@test.com";

        User user = new User();
        user.setFullName("Concurrency Test");
        user.setEmail(email);

        user = userRepository.saveAndFlush(user);

        Account account = new Account();
        account.setAccountNumber(
                "ACC-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        account = accountRepository.saveAndFlush(account);

        Long accountId = account.getId();
        Long userId = user.getId();

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {

            Future<String> firstWithdraw = executor.submit(() -> {
                ready.countDown();
                start.await();

                try {
                    accountService.withdraw(
                            accountId,
                            new BigDecimal("800.00"),
                            email
                    );

                    return "SUCCESS";

                } catch (InsufficientBalanceException exception) {
                    return "INSUFFICIENT";
                }
            });

            Future<String> secondWithdraw = executor.submit(() -> {
                ready.countDown();
                start.await();

                try {
                    accountService.withdraw(
                            accountId,
                            new BigDecimal("800.00"),
                            email
                    );

                    return "SUCCESS";

                } catch (InsufficientBalanceException exception) {
                    return "INSUFFICIENT";
                }
            });

            ready.await();

            start.countDown();

            String firstResult = firstWithdraw.get();
            String secondResult = secondWithdraw.get();

            long successCount = List.of(firstResult, secondResult)
                    .stream()
                    .filter("SUCCESS"::equals)
                    .count();

            long insufficientCount = List.of(firstResult, secondResult)
                    .stream()
                    .filter("INSUFFICIENT"::equals)
                    .count();

            assertEquals(1, successCount);
            assertEquals(1, insufficientCount);

            Account finalAccount = accountRepository.findById(accountId)
                    .orElseThrow();

            assertEquals(
                    new BigDecimal("200.00"),
                    finalAccount.getBalance()
            );

            List<Transaction> transactions =
                    transactionRepository
                            .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                                    accountId,
                                    accountId
                            );

            assertEquals(1, transactions.size());

        } finally {

            executor.shutdownNow();

            List<Transaction> transactions =
                    transactionRepository
                            .findByAccountIdOrTargetAccountIdOrderByCreatedAtDesc(
                                    accountId,
                                    accountId
                            );

            transactionRepository.deleteAll(transactions);
            accountRepository.deleteById(accountId);
            userRepository.deleteById(userId);
        }
    }
}