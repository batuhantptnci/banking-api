package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.AuthResponse;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.LoginRequest;
import com.batuhan.bankingapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody CreateUserRequest request
    ) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }
}