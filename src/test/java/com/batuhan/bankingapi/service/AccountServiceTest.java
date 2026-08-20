package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.TransactionType;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.AccountAccessDeniedException;
import com.batuhan.bankingapi.exception.InsufficientBalanceException;
import com.batuhan.bankingapi.exception.InvalidTransferException;
import com.batuhan.bankingapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountService = new AccountService(
                accountRepository,
                userService,
                transactionService
        );
    }

    @Test
    void shouldDepositMoneySuccessfully() {

        User user = new User();
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        Account result = accountService.deposit(
                1L,
                new BigDecimal("500.00"),
                "test@test.com"
        );

        assertEquals(
                new BigDecimal("600.00"),
                result.getBalance()
        );
    }

    @Test
    void shouldWithdrawMoneySuccessfully() {

        User user = new User();
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("600.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        Account result = accountService.withdraw(
                1L,
                new BigDecimal("100.00"),
                "test@test.com"
        );

        assertEquals(
                new BigDecimal("500.00"),
                result.getBalance()
        );
    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {

        User user = new User();
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.withdraw(
                        1L,
                        new BigDecimal("500.00"),
                        "test@test.com"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnAccount() {

        User user = new User();
        user.setEmail("owner@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("500.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountAccessDeniedException.class,
                () -> accountService.withdraw(
                        1L,
                        new BigDecimal("100.00"),
                        "hacker@test.com"
                )
        );
    }

    @Test
    void shouldTransferMoneySuccessfully() {

        User senderUser = new User();
        senderUser.setEmail("sender@test.com");

        User receiverUser = new User();
        receiverUser.setEmail("receiver@test.com");

        Account senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setBalance(new BigDecimal("1000.00"));
        senderAccount.setUser(senderUser);

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setUser(receiverUser);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(receiverAccount));

        accountService.transfer(
                1L,
                2L,
                new BigDecimal("300.00"),
                "sender@test.com"
        );

        assertEquals(
                new BigDecimal("700.00"),
                senderAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("500.00"),
                receiverAccount.getBalance()
        );
    }

    @Test
    void shouldNotTransferWhenBalanceIsInsufficient() {

        User senderUser = new User();
        senderUser.setEmail("sender@test.com");

        User receiverUser = new User();
        receiverUser.setEmail("receiver@test.com");

        Account senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setBalance(new BigDecimal("100.00"));
        senderAccount.setUser(senderUser);

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setUser(receiverUser);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(receiverAccount));

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.transfer(
                        1L,
                        2L,
                        new BigDecimal("500.00"),
                        "sender@test.com"
                )
        );

        assertEquals(
                new BigDecimal("100.00"),
                senderAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("200.00"),
                receiverAccount.getBalance()
        );
    }

    @Test
    void shouldThrowExceptionWhenTransferringToSameAccount() {

        assertThrows(
                InvalidTransferException.class,
                () -> accountService.transfer(
                        1L,
                        1L,
                        new BigDecimal("100.00"),
                        "test@test.com"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenTransferringFromAnotherUsersAccount() {

        User owner = new User();
        owner.setEmail("owner@test.com");

        User receiverUser = new User();
        receiverUser.setEmail("receiver@test.com");

        Account senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setBalance(new BigDecimal("1000.00"));
        senderAccount.setUser(owner);

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setUser(receiverUser);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(receiverAccount));

        assertThrows(
                AccountAccessDeniedException.class,
                () -> accountService.transfer(
                        1L,
                        2L,
                        new BigDecimal("100.00"),
                        "hacker@test.com"
                )
        );

        assertEquals(
                new BigDecimal("1000.00"),
                senderAccount.getBalance()
        );
    }

    @Test
    void shouldCreateTransactionWhenDepositingMoney() {

        User user = new User();
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        accountService.deposit(
                1L,
                new BigDecimal("500.00"),
                "test@test.com"
        );

        verify(transactionService).createTransaction(
                TransactionType.DEPOSIT,
                new BigDecimal("500.00"),
                account,
                null
        );
    }

    @Test
    void shouldCreateTransactionWhenWithdrawingMoney() {

        User user = new User();
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(1L);
        account.setBalance(new BigDecimal("600.00"));
        account.setUser(user);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        accountService.withdraw(
                1L,
                new BigDecimal("100.00"),
                "test@test.com"
        );

        verify(transactionService).createTransaction(
                TransactionType.WITHDRAW,
                new BigDecimal("100.00"),
                account,
                null
        );
    }

    @Test
    void shouldCreateTransactionWhenTransferringMoney() {

        User senderUser = new User();
        senderUser.setEmail("sender@test.com");

        User receiverUser = new User();
        receiverUser.setEmail("receiver@test.com");

        Account senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setBalance(new BigDecimal("1000.00"));
        senderAccount.setUser(senderUser);

        Account receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setUser(receiverUser);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(senderAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(receiverAccount));

        accountService.transfer(
                1L,
                2L,
                new BigDecimal("300.00"),
                "sender@test.com"
        );

        verify(transactionService).createTransaction(
                TransactionType.TRANSFER,
                new BigDecimal("300.00"),
                senderAccount,
                receiverAccount
        );
    }
}