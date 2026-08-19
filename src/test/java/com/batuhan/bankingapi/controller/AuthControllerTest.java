package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.AuthResponse;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.UserResponse;
import com.batuhan.bankingapi.service.AuthService;
import com.batuhan.bankingapi.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.batuhan.bankingapi.dto.LoginRequest;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        AuthResponse response = new AuthResponse(
                "fake-jwt-token",
                new UserResponse(
                        1L,
                        "Test User",
                        "test@test.com"
                )
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "email": "test@test.com",
                                      "password": "12345678"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("fake-jwt-token"))
                .andExpect(jsonPath("$.user.id")
                        .value(1))
                .andExpect(jsonPath("$.user.email")
                        .value("test@test.com"));
    }
    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "email": "bozuk-email",
                                      "password": "12345678"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturnBadRequestWhenPasswordIsBlank() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "email": "test@test.com",
                                      "password": ""
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldRegisterSuccessfully() throws Exception {

        AuthResponse response = new AuthResponse(
                "register-jwt-token",
                new UserResponse(
                        2L,
                        "Batuhan Test",
                        "batuhan@test.com"
                )
        );

        when(authService.register(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Batuhan Test",
                                      "email": "batuhan@test.com",
                                      "password": "12345678"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token")
                        .value("register-jwt-token"))
                .andExpect(jsonPath("$.user.id")
                        .value(2))
                .andExpect(jsonPath("$.user.fullName")
                        .value("Batuhan Test"))
                .andExpect(jsonPath("$.user.email")
                        .value("batuhan@test.com"));
    }
    @Test
    void shouldReturnBadRequestWhenRegisterPasswordIsTooShort() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "fullName": "Batuhan Test",
                                      "email": "batuhan@test.com",
                                      "password": "123"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest());
    }
}

