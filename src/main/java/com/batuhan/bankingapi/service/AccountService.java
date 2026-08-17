package com.batuhan.bankingapi.service;

import org.springframework.stereotype.Service;

import com.batuhan.bankingapi.repository.AccountRepository;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.AccountNotFoundException;
import com.batuhan.bankingapi.exception.InsufficientBalanceException;
import org.springframework.transaction.annotation.Transactional;
import com.batuhan.bankingapi.exception.InvalidTransferException;
import com.batuhan.bankingapi.entity.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final TransactionService transactionService;

    public AccountService(AccountRepository accountRepository, UserService userService, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.transactionService = transactionService;
    }
    public Account createAccount(Long userId) {

        User user = userService.getUserById(userId);

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

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public List<Account> getAccountsByUserId(Long userId) {
        userService.getUserById(userId);

        return accountRepository.findByUserId(userId);
    }
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Hesap bulunamadı"));
    }
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {

        Account account = getAccountById(accountId);

        BigDecimal newBalance = account.getBalance().add(amount);

        account.setBalance(newBalance);

        Account savedAccount = accountRepository.save(account);

        transactionService.createTransaction(
                TransactionType.DEPOSIT,
                amount,
                savedAccount,
                null
        );

        return savedAccount;
    }
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {

        Account account = getAccountById(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Yetersiz bakiye");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);

        account.setBalance(newBalance);

        Account savedAccount = accountRepository.save(account);

        transactionService.createTransaction(
                TransactionType.WITHDRAW,
                amount,
                savedAccount,
                null
        );

        return savedAccount;
    }
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {

        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidTransferException(
                    "Gönderen ve alıcı hesap aynı olamaz"
            );
        }

        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Yetersiz bakiye");
        }

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(amount)
        );

        toAccount.setBalance(
                toAccount.getBalance().add(amount)
        );

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transactionService.createTransaction(
                TransactionType.TRANSFER,
                amount,
                fromAccount,
                toAccount
        );
    }


}