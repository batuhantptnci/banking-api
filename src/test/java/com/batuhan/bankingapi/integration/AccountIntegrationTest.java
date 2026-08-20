package com.batuhan.bankingapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class AccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAccountForAuthenticatedUser() throws Exception {

        String email = "account-integration-" + System.currentTimeMillis() + "@test.com";

        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Account Integration Test",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(registerResponse, "$.token");

        mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accountNumber").isNotEmpty())
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.userId").exists());
    }
    @Test
    void shouldDepositAndWithdrawSuccessfully() throws Exception {

        String email = "money-integration-" + System.currentTimeMillis() + "@test.com";

        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Money Integration Test",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(registerResponse, "$.token");

        String accountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer accountId = JsonPath.read(accountResponse, "$.id");

        // 💰 1000 yatır
        mockMvc.perform(
                        post("/api/accounts/{id}/deposit", accountId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));

        // 💸 250 çek
        mockMvc.perform(
                        post("/api/accounts/{id}/withdraw", accountId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "amount": 250
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(750));
    }
    @Test
    void shouldTransferMoneyBetweenAccountsSuccessfully() throws Exception {

        String email = "transfer-integration-" + System.currentTimeMillis() + "@test.com";

        // Kullanıcı oluştur + JWT al
        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Transfer Integration Test",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(registerResponse, "$.token");

        // 1. hesap
        String firstAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer firstAccountId = JsonPath.read(firstAccountResponse, "$.id");

        // 2. hesap
        String secondAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer secondAccountId = JsonPath.read(secondAccountResponse, "$.id");

        // İlk hesaba 1000 yatır
        mockMvc.perform(
                        post("/api/accounts/{id}/deposit", firstAccountId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk());

        // İlk hesaptan ikinci hesaba 300 transfer
        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fromAccountId": %d,
                                      "toAccountId": %d,
                                      "amount": 300
                                    }
                                    """.formatted(firstAccountId, secondAccountId))
                )
                .andExpect(status().isOk());

        // İlk hesap 700 kaldı mı?
        mockMvc.perform(
                        get("/api/accounts/{id}", firstAccountId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(700));

        // İkinci hesap 300 oldu mu?
        mockMvc.perform(
                        get("/api/accounts/{id}", secondAccountId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(300));
    }
    @Test
    void shouldReturnTransactionHistorySuccessfully() throws Exception {

        String email = "history-integration-" + System.currentTimeMillis() + "@test.com";

        // Kullanıcı oluştur + JWT al
        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "History Integration Test",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(registerResponse, "$.token");

        // 1. hesap
        String firstAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer firstAccountId = JsonPath.read(firstAccountResponse, "$.id");

        // 2. hesap
        String secondAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer secondAccountId = JsonPath.read(secondAccountResponse, "$.id");

        // İlk hesaba 1000 yatır
        mockMvc.perform(
                        post("/api/accounts/{id}/deposit", firstAccountId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk());

        // 300 transfer et
        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fromAccountId": %d,
                                      "toAccountId": %d,
                                      "amount": 300
                                    }
                                    """.formatted(firstAccountId, secondAccountId))
                )
                .andExpect(status().isOk());

        // Gönderen hesabın history'si
        mockMvc.perform(
                        get("/api/transactions/account/{accountId}", firstAccountId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$[0].direction").value("OUTGOING"))
                .andExpect(jsonPath("$[0].amount").value(300))
                .andExpect(jsonPath("$[0].accountId").value(firstAccountId))
                .andExpect(jsonPath("$[0].targetAccountId").value(secondAccountId))
                .andExpect(jsonPath("$[1].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].direction").value("INCOMING"))
                .andExpect(jsonPath("$[1].amount").value(1000));

        // Alıcı hesabın history'si
        mockMvc.perform(
                        get("/api/transactions/account/{accountId}", secondAccountId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$[0].direction").value("INCOMING"))
                .andExpect(jsonPath("$[0].amount").value(300));
    }
}