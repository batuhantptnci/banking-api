package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.AccountAccessDeniedException;
import com.batuhan.bankingapi.exception.AccountNotFoundException;
import com.batuhan.bankingapi.exception.InsufficientBalanceException;
import com.batuhan.bankingapi.exception.InvalidTransferException;
import com.batuhan.bankingapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TransactionService transactionService;

    public AccountService(
            AccountRepository accountRepository,
            UserService userService,
            TransactionService transactionService
    ) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    public Account createAccount(String userEmail) {

        User user = userService.getUserByEmail(userEmail);

        Account account = new Account();

        account.setAccountNumber(
                "ACC-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {

        userService.getUserById(userId);

        return accountRepository.findByUserId(userId);
    }

    public Account getAccountById(Long id) {

        return accountRepository.findById(id)
                .orElseThrow(
                        () -> new AccountNotFoundException(
                                "Hesap bulunamadı"
                        )
                );
    }

    public Account getAccountByAccountNumber(
            String accountNumber
    ) {

        String normalizedAccountNumber =
                accountNumber.trim().toUpperCase();

        return accountRepository
                .findByAccountNumber(normalizedAccountNumber)
                .orElseThrow(
                        () -> new AccountNotFoundException(
                                "Alıcı hesap bulunamadı"
                        )
                );
    }

    @Transactional
    public Transaction deposit(
            Long accountId,
            BigDecimal amount,
            String userEmail
    ) {

        Account account =
                getOwnedAccountForUpdate(
                        accountId,
                        userEmail
                );

        account.setBalance(
                account.getBalance().add(amount)
        );

        Account savedAccount =
                accountRepository.save(account);

        return transactionService.createTransaction(
                TransactionType.DEPOSIT,
                amount,
                savedAccount,
                null
        );
    }

    @Transactional
    public Transaction withdraw(
            Long accountId,
            BigDecimal amount,
            String userEmail
    ) {

        Account account =
                getOwnedAccountForUpdate(
                        accountId,
                        userEmail
                );

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Yetersiz bakiye"
            );
        }

        account.setBalance(
                account.getBalance().subtract(amount)
        );

        Account savedAccount =
                accountRepository.save(account);

        return transactionService.createTransaction(
                TransactionType.WITHDRAW,
                amount,
                savedAccount,
                null
        );
    }

    @Transactional
    public Transaction transfer(
            Long fromAccountId,
            String toAccountNumber,
            BigDecimal amount,
            String userEmail
    ) {

        String normalizedAccountNumber =
                toAccountNumber
                        .trim()
                        .toUpperCase();

        Account destinationAccount =
                accountRepository
                        .findByAccountNumber(
                                normalizedAccountNumber
                        )
                        .orElseThrow(
                                () -> new AccountNotFoundException(
                                        "Alıcı hesap bulunamadı"
                                )
                        );

        Long toAccountId =
                destinationAccount.getId();

        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidTransferException(
                    "Gönderen ve alıcı hesap aynı olamaz"
            );
        }

        // Deadlock riskini azaltmak için
        // her zaman küçük ID önce lock edilir.
        Long firstId =
                Math.min(
                        fromAccountId,
                        toAccountId
                );

        Long secondId =
                Math.max(
                        fromAccountId,
                        toAccountId
                );

        Account firstAccount =
                getAccountForUpdate(firstId);

        Account secondAccount =
                getAccountForUpdate(secondId);

        Account fromAccount =
                fromAccountId.equals(firstId)
                        ? firstAccount
                        : secondAccount;

        Account toAccount =
                toAccountId.equals(firstId)
                        ? firstAccount
                        : secondAccount;

        if (!fromAccount
                .getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new AccountAccessDeniedException(
                    "Bu hesaptan transfer yapma yetkiniz yok"
            );
        }

        if (fromAccount
                .getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Yetersiz bakiye"
            );
        }

        fromAccount.setBalance(
                fromAccount
                        .getBalance()
                        .subtract(amount)
        );

        toAccount.setBalance(
                toAccount
                        .getBalance()
                        .add(amount)
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return transactionService.createTransaction(
                TransactionType.TRANSFER,
                amount,
                fromAccount,
                toAccount
        );
    }

    public Account getOwnedAccount(
            Long accountId,
            String userEmail
    ) {

        Account account =
                getAccountById(accountId);

        if (!account
                .getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new AccountAccessDeniedException(
                    "Bu hesaba erişim yetkiniz yok"
            );
        }

        return account;
    }

    public Account getOwnedAccountForUpdate(
            Long accountId,
            String userEmail
    ) {

        Account account =
                accountRepository
                        .findByIdForUpdate(accountId)
                        .orElseThrow(
                                () -> new AccountNotFoundException(
                                        "Hesap bulunamadı"
                                )
                        );

        if (!account
                .getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new AccountAccessDeniedException(
                    "Bu hesaba erişim yetkiniz yok"
            );
        }

        return account;
    }

    public Account getAccountForUpdate(
            Long accountId
    ) {

        return accountRepository
                .findByIdForUpdate(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundException(
                                "Hesap bulunamadı"
                        )
                );
    }

    public Account createDefaultAccount(User user) {

        Account account = new Account();

        account.setAccountNumber(
                "ACC-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        return accountRepository.save(account);
    }
}