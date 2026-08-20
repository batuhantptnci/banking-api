package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.LoginRequest;
import com.batuhan.bankingapi.entity.Role;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.InvalidCredentialsException;
import com.batuhan.bankingapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                userService
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("12345678");

        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "12345678",
                "$2a$10$fakeHash"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "test@test.com",
                Role.USER
        )).thenReturn("fake-jwt-token");

        var response = authService.login(request);

        assertNotNull(response);

        verify(jwtService, times(1))
                .generateToken(
                        "test@test.com",
                        Role.USER
                );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "$2a$10$fakeHash"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        any(Role.class)
                );
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("12345678");

        when(userRepository.findByEmail("notfound@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        any(Role.class)
                );
    }

    @Test
    void shouldNotGenerateTokenWhenPasswordIsWrong() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "$2a$10$fakeHash"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never())
                .generateToken(
                        anyString(),
                        any(Role.class)
                );
    }

    @Test
    void shouldGenerateTokenOnceWhenLoginIsSuccessful() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("12345678");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "12345678",
                "$2a$10$fakeHash"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "test@test.com",
                Role.USER
        )).thenReturn("fake-jwt-token");

        authService.login(request);

        verify(jwtService, times(1))
                .generateToken(
                        "test@test.com",
                        Role.USER
                );
    }
}