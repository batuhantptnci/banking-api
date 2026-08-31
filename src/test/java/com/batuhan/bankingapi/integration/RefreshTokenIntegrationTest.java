package com.batuhan.bankingapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void shouldRotateRefreshTokenAndRejectOldToken()
            throws Exception {

        String email =
                "refresh-" +
                        UUID.randomUUID() +
                        "@test.com";

        String registerBody =
                IntegrationTestData.registerBody(
                        "Refresh Test",
                        email
                );

        MvcResult registerResult =
                mockMvc.perform(
                                post("/api/auth/register")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                registerBody
                                        )
                        )
                        .andExpect(
                                status().is2xxSuccessful()
                        )
                        .andReturn();

        String oldRefreshToken =
                jsonMapper
                        .readTree(
                                registerResult
                                        .getResponse()
                                        .getContentAsString()
                        )
                        .get("refreshToken")
                        .asText();

        String refreshBody = """
                {
                  "refreshToken": "%s"
                }
                """.formatted(
                oldRefreshToken
        );

        MvcResult refreshResult =
                mockMvc.perform(
                                post("/api/auth/refresh")
                                        .contentType(
                                                MediaType.APPLICATION_JSON
                                        )
                                        .content(
                                                refreshBody
                                        )
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
                        .andReturn();

        String newRefreshToken =
                jsonMapper
                        .readTree(
                                refreshResult
                                        .getResponse()
                                        .getContentAsString()
                        )
                        .get("refreshToken")
                        .asText();

        assertNotEquals(
                oldRefreshToken,
                newRefreshToken
        );

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        refreshBody
                                )
                )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Geçersiz refresh token"
                                )
                );
    }
}