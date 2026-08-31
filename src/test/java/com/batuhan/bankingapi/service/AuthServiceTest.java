package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.LoginRequest;
import com.batuhan.bankingapi.entity.RefreshToken;
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
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                userService,
                refreshTokenService,
                accountService
        );
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();

        request.setIdentifier("12345678");
        request.setPassword("12345678");

        User user = new User();

        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setCustomerNumber("12345678");
        user.setNationalId("11111111111");
        user.setPhone("5551112233");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                "fake-refresh-token"
        );

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "12345678",
                                "12345678"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "12345678",
                        "$2a$10$fakeHash"
                )
        ).thenReturn(true);

        when(
                jwtService.generateToken(
                        "test@test.com",
                        Role.USER
                )
        ).thenReturn(
                "fake-jwt-token"
        );

        when(
                refreshTokenService
                        .createRefreshToken(user)
        ).thenReturn(
                refreshToken
        );

        var response =
                authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertNotNull(
                response.getRefreshToken()
        );

        verify(
                userRepository,
                times(1)
        ).findByCustomerNumberOrNationalId(
                "12345678",
                "12345678"
        );

        verify(
                jwtService,
                times(1)
        ).generateToken(
                "test@test.com",
                Role.USER
        );

        verify(
                refreshTokenService,
                times(1)
        ).createRefreshToken(user);
    }

    @Test
    void shouldLoginSuccessfullyWithNationalId() {

        LoginRequest request =
                new LoginRequest();

        request.setIdentifier(
                "11111111111"
        );

        request.setPassword(
                "12345678"
        );

        User user = new User();

        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");
        user.setCustomerNumber("12345678");
        user.setNationalId("11111111111");
        user.setPhone("5551112233");
        user.setPassword("$2a$10$fakeHash");
        user.setRole(Role.USER);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                "fake-refresh-token"
        );

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "11111111111",
                                "11111111111"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "12345678",
                        "$2a$10$fakeHash"
                )
        ).thenReturn(true);

        when(
                jwtService.generateToken(
                        "test@test.com",
                        Role.USER
                )
        ).thenReturn(
                "fake-jwt-token"
        );

        when(
                refreshTokenService
                        .createRefreshToken(user)
        ).thenReturn(
                refreshToken
        );

        var response =
                authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());

        verify(
                userRepository
        ).findByCustomerNumberOrNationalId(
                "11111111111",
                "11111111111"
        );
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsWrong() {

        LoginRequest request =
                new LoginRequest();

        request.setIdentifier(
                "12345678"
        );

        request.setPassword(
                "wrongPassword"
        );

        User user = new User();

        user.setEmail(
                "test@test.com"
        );

        user.setCustomerNumber(
                "12345678"
        );

        user.setPassword(
                "$2a$10$fakeHash"
        );

        user.setRole(Role.USER);

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "12345678",
                                "12345678"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "wrongPassword",
                        "$2a$10$fakeHash"
                )
        ).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(
                jwtService,
                never()
        ).generateToken(
                anyString(),
                any(Role.class)
        );

        verify(
                refreshTokenService,
                never()
        ).createRefreshToken(
                any(User.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenIdentifierDoesNotExist() {

        LoginRequest request =
                new LoginRequest();

        request.setIdentifier(
                "12345678"
        );

        request.setPassword(
                "12345678"
        );

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "12345678",
                                "12345678"
                        )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(
                jwtService,
                never()
        ).generateToken(
                anyString(),
                any(Role.class)
        );

        verify(
                refreshTokenService,
                never()
        ).createRefreshToken(
                any(User.class)
        );
    }

    @Test
    void shouldNotGenerateTokenWhenPasswordIsWrong() {

        LoginRequest request =
                new LoginRequest();

        request.setIdentifier(
                "12345678"
        );

        request.setPassword(
                "wrongPassword"
        );

        User user = new User();

        user.setEmail(
                "test@test.com"
        );

        user.setCustomerNumber(
                "12345678"
        );

        user.setPassword(
                "$2a$10$fakeHash"
        );

        user.setRole(Role.USER);

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "12345678",
                                "12345678"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "wrongPassword",
                        "$2a$10$fakeHash"
                )
        ).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(
                jwtService,
                never()
        ).generateToken(
                anyString(),
                any(Role.class)
        );

        verify(
                refreshTokenService,
                never()
        ).createRefreshToken(
                any(User.class)
        );
    }

    @Test
    void shouldGenerateTokenOnceWhenLoginIsSuccessful() {

        LoginRequest request =
                new LoginRequest();

        request.setIdentifier(
                "12345678"
        );

        request.setPassword(
                "12345678"
        );

        User user = new User();

        user.setId(1L);
        user.setFullName(
                "Test User"
        );

        user.setEmail(
                "test@test.com"
        );

        user.setCustomerNumber(
                "12345678"
        );

        user.setNationalId(
                "11111111111"
        );

        user.setPhone(
                "5551112233"
        );

        user.setPassword(
                "$2a$10$fakeHash"
        );

        user.setRole(Role.USER);

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                "fake-refresh-token"
        );

        when(
                userRepository
                        .findByCustomerNumberOrNationalId(
                                "12345678",
                                "12345678"
                        )
        ).thenReturn(
                Optional.of(user)
        );

        when(
                passwordEncoder.matches(
                        "12345678",
                        "$2a$10$fakeHash"
                )
        ).thenReturn(true);

        when(
                jwtService.generateToken(
                        "test@test.com",
                        Role.USER
                )
        ).thenReturn(
                "fake-jwt-token"
        );

        when(
                refreshTokenService
                        .createRefreshToken(user)
        ).thenReturn(
                refreshToken
        );

        authService.login(request);

        verify(
                jwtService,
                times(1)
        ).generateToken(
                "test@test.com",
                Role.USER
        );

        verify(
                refreshTokenService,
                times(1)
        ).createRefreshToken(user);
    }
}