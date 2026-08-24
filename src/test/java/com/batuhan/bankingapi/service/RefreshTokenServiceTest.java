package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.RefreshToken;
import com.batuhan.bankingapi.exception.InvalidRefreshTokenException;
import com.batuhan.bankingapi.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        refreshTokenService =
                new RefreshTokenService(refreshTokenRepository);
    }

    @Test
    void shouldValidateRefreshTokenSuccessfully() throws Exception {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-token");
        refreshToken.setExpiresAt(
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenRepository
                .findByTokenHashForUpdate(hash("valid-token")))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.validateRefreshToken("valid-token");

        assertNotNull(result);
        assertEquals("valid-token", result.getToken());

        verify(refreshTokenRepository, never())
                .delete(any());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenDoesNotExist() throws Exception {

        when(refreshTokenRepository
                .findByTokenHashForUpdate(hash("invalid-token")))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenService
                        .validateRefreshToken("invalid-token")
        );
    }

    @Test
    void shouldDeleteAndThrowWhenRefreshTokenIsExpired() throws Exception {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expired-token");
        refreshToken.setExpiresAt(
                LocalDateTime.now().minusDays(1)
        );

        when(refreshTokenRepository
                .findByTokenHashForUpdate(hash("expired-token")))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenService
                        .validateRefreshToken("expired-token")
        );

        verify(refreshTokenRepository, times(1))
                .delete(refreshToken);
    }
    private String hash(String token) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

        byte[] hashed = digest.digest(
                token.getBytes(StandardCharsets.UTF_8)
        );

        return HexFormat.of().formatHex(hashed);
    }
}