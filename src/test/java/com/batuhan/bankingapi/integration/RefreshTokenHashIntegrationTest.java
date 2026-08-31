package com.batuhan.bankingapi.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenHashIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldStoreRefreshTokenAsHash()
            throws Exception {

        String email =
                "hash-" +
                        UUID.randomUUID() +
                        "@test.com";

        String registerBody =
                IntegrationTestData.registerBody(
                        "Hash Test",
                        email
                );

        MvcResult result =
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

        String rawRefreshToken =
                jsonMapper
                        .readTree(
                                result
                                        .getResponse()
                                        .getContentAsString()
                        )
                        .get("refreshToken")
                        .asText();

        String storedTokenHash =
                jdbcTemplate.queryForObject(
                        """
                        SELECT rt.token_hash
                        FROM refresh_tokens rt
                        JOIN users u
                          ON u.id = rt.user_id
                        WHERE u.email = ?
                        """,
                        String.class,
                        email
                );

        assertNotEquals(
                rawRefreshToken,
                storedTokenHash
        );

        assertEquals(
                64,
                storedTokenHash.length()
        );

        assertEquals(
                sha256(rawRefreshToken),
                storedTokenHash
        );
    }

    private String sha256(
            String value
    ) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-256"
                );

        byte[] hash =
                digest.digest(
                        value.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return HexFormat
                .of()
                .formatHex(hash);
    }
}