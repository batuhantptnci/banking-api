package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.service.JwtService;
import com.batuhan.bankingapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    // =========================================================================
    // MY ACCOUNTS
    // =========================================================================

    @Test
    void shouldGetMyAccountsSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("1500.00"));
        account.setUser(user);

        when(userService.getUserByEmail("test@test.com"))
                .thenReturn(user);

        when(accountService.getAccountsByUserId(1L))
                .thenReturn(List.of(account));

        mockMvc.perform(
                        get("/api/accounts/me")
                                .principal(() -> "test@test.com")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$[0].accountNumber")
                                .value("ACC-TEST1234")
                )
                .andExpect(
                        jsonPath("$[0].balance")
                                .value(1500.00)
                )
                .andExpect(
                        jsonPath("$[0].userId")
                                .value(1)
                );
    }

    // =========================================================================
    // OWNED ACCOUNT
    // =========================================================================

    @Test
    void shouldGetOwnedAccountSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("1500.00"));
        account.setUser(user);

        when(
                accountService.getOwnedAccount(
                        10L,
                        "test@test.com"
                )
        ).thenReturn(account);

        mockMvc.perform(
                        get("/api/accounts/10")
                                .principal(() -> "test@test.com")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("ACC-TEST1234")
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(1500.00)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(1)
                );
    }

    // =========================================================================
    // DEPOSIT
    // =========================================================================

    @Test
    void shouldDepositMoneySuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("2000.00"));
        account.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setAccount(account);
        transaction.setSourceBalanceAfter(
                new BigDecimal("2000.00")
        );

        when(
                accountService.deposit(
                        10L,
                        new BigDecimal("500.00"),
                        "test@test.com"
                )
        ).thenReturn(transaction);

        mockMvc.perform(
                        post("/api/accounts/10/deposit")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "amount": 500.00
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.type")
                                .value("DEPOSIT")
                )
                .andExpect(
                        jsonPath("$.direction")
                                .value("INCOMING")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500.00)
                )
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("ACC-TEST1234")
                )
                .andExpect(
                        jsonPath("$.balanceAfter")
                                .value(2000.00)
                );
    }

    // =========================================================================
    // WITHDRAW
    // =========================================================================

    @Test
    void shouldWithdrawMoneySuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(101L);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setAccount(account);
        transaction.setSourceBalanceAfter(
                new BigDecimal("1000.00")
        );

        when(
                accountService.withdraw(
                        10L,
                        new BigDecimal("500.00"),
                        "test@test.com"
                )
        ).thenReturn(transaction);

        mockMvc.perform(
                        post("/api/accounts/10/withdraw")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "amount": 500.00
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(101)
                )
                .andExpect(
                        jsonPath("$.type")
                                .value("WITHDRAW")
                )
                .andExpect(
                        jsonPath("$.direction")
                                .value("OUTGOING")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(500.00)
                )
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("ACC-TEST1234")
                )
                .andExpect(
                        jsonPath("$.balanceAfter")
                                .value(1000.00)
                );
    }

    // =========================================================================
    // TRANSFER
    // =========================================================================

    @Test
    void shouldTransferMoneySuccessfully() throws Exception {

        User sender = new User();
        sender.setId(1L);
        sender.setEmail("test@test.com");
        sender.setFullName("Test User");

        User receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@test.com");
        receiver.setFullName("Receiver User");

        Account fromAccount = new Account();
        fromAccount.setId(10L);
        fromAccount.setAccountNumber("ACC-SEND1234");
        fromAccount.setBalance(new BigDecimal("1700.00"));
        fromAccount.setUser(sender);

        Account toAccount = new Account();
        toAccount.setId(20L);
        toAccount.setAccountNumber("ACC-RECV1234");
        toAccount.setBalance(new BigDecimal("800.00"));
        toAccount.setUser(receiver);

        Transaction transaction = new Transaction();
        transaction.setId(102L);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(new BigDecimal("300.00"));
        transaction.setAccount(fromAccount);
        transaction.setTargetAccount(toAccount);
        transaction.setSourceBalanceAfter(
                new BigDecimal("1700.00")
        );
        transaction.setTargetBalanceAfter(
                new BigDecimal("800.00")
        );

        when(
                accountService.transfer(
                        10L,
                        "ACC-RECV1234",
                        new BigDecimal("300.00"),
                        "test@test.com"
                )
        ).thenReturn(transaction);

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "fromAccountId": 10,
                                          "toAccountNumber": "ACC-RECV1234",
                                          "amount": 300.00
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(102)
                )
                .andExpect(
                        jsonPath("$.type")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$.direction")
                                .value("OUTGOING")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(300.00)
                )
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("ACC-SEND1234")
                )
                .andExpect(
                        jsonPath("$.targetAccountId")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.targetAccountNumber")
                                .value("ACC-RECV1234")
                )
                .andExpect(
                        jsonPath("$.targetAccountHolderName")
                                .value("Receiver User")
                )
                .andExpect(
                        jsonPath("$.balanceAfter")
                                .value(1700.00)
                );

        verify(accountService).transfer(
                10L,
                "ACC-RECV1234",
                new BigDecimal("300.00"),
                "test@test.com"
        );
    }

    @Test
    void shouldReturnBadRequestWhenTransferAmountIsNegative()
            throws Exception {

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "fromAccountId": 10,
                                          "toAccountNumber": "ACC-RECV1234",
                                          "amount": -300.00
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(
                accountService,
                never()
        ).transfer(
                anyLong(),
                anyString(),
                any(BigDecimal.class),
                anyString()
        );
    }
}