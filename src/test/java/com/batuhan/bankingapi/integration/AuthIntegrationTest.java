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
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterAndLoginSuccessfully()
            throws Exception {

        String email =
                "integration-" +
                        System.nanoTime() +
                        "@test.com";

        String nationalId =
                IntegrationTestData.nationalId();

        String phone =
                IntegrationTestData.phone();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        IntegrationTestData.registerBody(
                                                "Integration Test",
                                                email,
                                                nationalId,
                                                phone
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.token")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.refreshToken")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.user.email")
                                .value(email)
                )
                .andExpect(
                        jsonPath("$.user.customerNumber")
                                .isNotEmpty()
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "identifier": "%s",
                                          "password": "12345678"
                                        }
                                        """.formatted(
                                        nationalId
                                ))
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.token")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.refreshToken")
                                .isNotEmpty()
                )
                .andExpect(
                        jsonPath("$.user.email")
                                .value(email)
                );
    }

    @Test
    void shouldAccessProtectedEndpointWithJwt()
            throws Exception {

        String email =
                "jwt-integration-" +
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
                                                        "JWT Integration Test",
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
                        get("/api/accounts/me")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void shouldReturnUnauthorizedWithoutJwt()
            throws Exception {

        mockMvc.perform(
                        get("/api/accounts/me")
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }
}