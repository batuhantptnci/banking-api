package com.batuhan.bankingapi.dto;

import lombok.Getter;

@Getter
public class AuthResponse {

    private String token;
    private String refreshToken;
    private UserResponse user;

    // Eski kodlar bozulmasın diye
    public AuthResponse(
            String token,
            UserResponse user
    ) {
        this.token = token;
        this.user = user;
    }

    // Refresh token'lı yeni response
    public AuthResponse(
            String token,
            String refreshToken,
            UserResponse user
    ) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}