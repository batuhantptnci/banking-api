package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.exception.AccountAccessDeniedException;
import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.service.JwtService;
import com.batuhan.bankingapi.service.TransactionService;
import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.Transaction;
import com.batuhan.bankingapi.entity.TransactionType;
import com.batuhan.bankingapi.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetAccountTransactionsSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(10L);
        account.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(100L);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("500.00"));
        transaction.setAccount(account);

        when(accountService.getOwnedAccount(
                10L,
                "test@test.com"
        )).thenReturn(account);

        when(transactionService.getTransactionsByAccountId(10L))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                        get("/api/transactions/account/10")
                                .principal(() -> "test@test.com")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].direction").value("INCOMING"))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[0].accountId").value(10));
    }
    @Test
    void shouldReturnForbiddenWhenAccessingAnotherUsersTransactions() throws Exception {

        when(accountService.getOwnedAccount(
                10L,
                "test@test.com"
        )).thenThrow(
                new AccountAccessDeniedException("Bu hesaba erişim yetkiniz yok")
        );

        mockMvc.perform(
                        get("/api/transactions/account/10")
                                .principal(() -> "test@test.com")
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("Bu hesaba erişim yetkiniz yok"));

        verify(transactionService, never())
                .getTransactionsByAccountId(anyLong());
    }
}