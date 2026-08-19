package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.service.AccountService;
import com.batuhan.bankingapi.service.JwtService;
import com.batuhan.bankingapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.batuhan.bankingapi.entity.Account;
import com.batuhan.bankingapi.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void shouldGetMyAccountsSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

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
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-TEST1234"))
                .andExpect(jsonPath("$[0].balance").value(1500.00))
                .andExpect(jsonPath("$[0].userId").value(1));
    }
    @Test
    void shouldGetOwnedAccountSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("1500.00"));
        account.setUser(user);

        when(accountService.getOwnedAccount(
                10L,
                "test@test.com"
        )).thenReturn(account);

        mockMvc.perform(
                        get("/api/accounts/10")
                                .principal(() -> "test@test.com")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.accountNumber").value("ACC-TEST1234"))
                .andExpect(jsonPath("$.balance").value(1500.00))
                .andExpect(jsonPath("$.userId").value(1));
    }
    @Test
    void shouldDepositMoneySuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("2000.00"));
        account.setUser(user);

        when(accountService.deposit(
                10L,
                new BigDecimal("500.00"),
                "test@test.com"
        )).thenReturn(account);

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
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.balance").value(2000.00))
                .andExpect(jsonPath("$.userId").value(1));
    }
    @Test
    void shouldWithdrawMoneySuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        Account account = new Account();
        account.setId(10L);
        account.setAccountNumber("ACC-TEST1234");
        account.setBalance(new BigDecimal("1000.00"));
        account.setUser(user);

        when(accountService.withdraw(
                10L,
                new BigDecimal("500.00"),
                "test@test.com"
        )).thenReturn(account);

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
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.balance").value(1000.00))
                .andExpect(jsonPath("$.userId").value(1));
    }
    @Test
    void shouldTransferMoneySuccessfully() throws Exception {

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fromAccountId": 10,
                                      "toAccountId": 20,
                                      "amount": 300.00
                                    }
                                    """)
                )
                .andExpect(status().isOk());

        verify(accountService).transfer(
                10L,
                20L,
                new BigDecimal("300.00"),
                "test@test.com"
        );
    }
    @Test
    void shouldReturnBadRequestWhenTransferAmountIsNegative() throws Exception {

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .principal(() -> "test@test.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fromAccountId": 10,
                                      "toAccountId": 20,
                                      "amount": -300.00
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());

        verify(accountService, never())
                .transfer(anyLong(), anyLong(), any(BigDecimal.class), anyString());
    }
}