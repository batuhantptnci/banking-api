package com.batuhan.bankingapi.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
    void shouldCreateAccountForAuthenticatedUser()
            throws Exception {

        String email =
                "account-integration-" +
                        System.nanoTime() +
                        "@test.com";

        String registerResponse =
                mockMvc.perform(
                                post("/api/auth/register")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                IntegrationTestData.registerBody(
                                                        "Account Integration Test",
                                                        email
                                                )
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String token =
                JsonPath.read(
                        registerResponse,
                        "$.token"
                );

        mockMvc.perform(
                        post("/api/accounts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .exists()
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .exists()
                );
    }

    @Test
    void shouldDepositAndWithdrawSuccessfully()
            throws Exception {

        String email =
                "money-integration-" +
                        System.nanoTime() +
                        "@test.com";

        String registerResponse =
                mockMvc.perform(
                                post("/api/auth/register")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                IntegrationTestData.registerBody(
                                                        "Money Integration Test",
                                                        email
                                                )
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String token =
                JsonPath.read(
                        registerResponse,
                        "$.token"
                );

        String accountResponse =
                mockMvc.perform(
                                post("/api/accounts")
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(
                                status().isCreated()
                        )
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        Integer accountId =
                JsonPath.read(
                        accountResponse,
                        "$.id"
                );

        mockMvc.perform(
                        post(
                                "/api/accounts/{id}/deposit",
                                accountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "amount": 1000
                                        }
                                        """)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(1000)
                );

        mockMvc.perform(
                        post(
                                "/api/accounts/{id}/withdraw",
                                accountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "amount": 250
                                        }
                                        """)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(750)
                );
    }

    @Test
    void shouldTransferMoneyBetweenAccountsSuccessfully() throws Exception {

        String email =
                "transfer-integration-"
                        + System.currentTimeMillis()
                        + "@test.com";

        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Transfer Integration Test",
                                      "nationalId": "88353526326",
                                      "phone": "5353526327",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token =
                JsonPath.read(registerResponse, "$.token");

        String firstAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer firstAccountId =
                JsonPath.read(
                        firstAccountResponse,
                        "$.id"
                );

        String secondAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer secondAccountId =
                JsonPath.read(
                        secondAccountResponse,
                        "$.id"
                );

        String secondAccountNumber =
                JsonPath.read(
                        secondAccountResponse,
                        "$.accountNumber"
                );

        mockMvc.perform(
                        post(
                                "/api/accounts/{id}/deposit",
                                firstAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                    {
                                      "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                    {
                                      "fromAccountId": %d,
                                      "toAccountNumber": "%s",
                                      "amount": 300
                                    }
                                    """.formatted(
                                        firstAccountId,
                                        secondAccountNumber
                                ))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get(
                                "/api/accounts/{id}",
                                firstAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.balance")
                                .value(700)
                );

        mockMvc.perform(
                        get(
                                "/api/accounts/{id}",
                                secondAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.balance")
                                .value(300)
                );
    }

    @Test
    void shouldReturnTransactionHistorySuccessfully() throws Exception {

        String email =
                "history-integration-"
                        + System.currentTimeMillis()
                        + "@test.com";

        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "History Integration Test",
                                      "nationalId": "88353526324",
                                      "phone": "5353526325",
                                      "email": "%s",
                                      "password": "12345678"
                                    }
                                    """.formatted(email))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token =
                JsonPath.read(registerResponse, "$.token");

        String firstAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer firstAccountId =
                JsonPath.read(
                        firstAccountResponse,
                        "$.id"
                );

        String secondAccountResponse = mockMvc.perform(
                        post("/api/accounts")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer secondAccountId =
                JsonPath.read(
                        secondAccountResponse,
                        "$.id"
                );

        String secondAccountNumber =
                JsonPath.read(
                        secondAccountResponse,
                        "$.accountNumber"
                );

        mockMvc.perform(
                        post(
                                "/api/accounts/{id}/deposit",
                                firstAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                    {
                                      "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/accounts/transfer")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                    {
                                      "fromAccountId": %d,
                                      "toAccountNumber": "%s",
                                      "amount": 300
                                    }
                                    """.formatted(
                                        firstAccountId,
                                        secondAccountNumber
                                ))
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get(
                                "/api/transactions/account/{accountId}",
                                firstAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].type")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$[0].direction")
                                .value("OUTGOING")
                )
                .andExpect(
                        jsonPath("$[0].amount")
                                .value(300)
                )
                .andExpect(
                        jsonPath("$[0].accountId")
                                .value(firstAccountId)
                )
                .andExpect(
                        jsonPath("$[0].targetAccountId")
                                .value(secondAccountId)
                )
                .andExpect(
                        jsonPath("$[1].type")
                                .value("DEPOSIT")
                )
                .andExpect(
                        jsonPath("$[1].direction")
                                .value("INCOMING")
                )
                .andExpect(
                        jsonPath("$[1].amount")
                                .value(1000)
                );

        mockMvc.perform(
                        get(
                                "/api/transactions/account/{accountId}",
                                secondAccountId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].type")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$[0].direction")
                                .value("INCOMING")
                )
                .andExpect(
                        jsonPath("$[0].amount")
                                .value(300)
                );
    }
}