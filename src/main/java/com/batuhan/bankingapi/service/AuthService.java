package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.AuthResponse;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.LoginRequest;
import com.batuhan.bankingapi.dto.RefreshTokenRequest;
import com.batuhan.bankingapi.entity.RefreshToken;
import com.batuhan.bankingapi.entity.Role;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.InvalidCredentialsException;
import com.batuhan.bankingapi.mapper.UserMapper;
import com.batuhan.bankingapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserService userService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(
            LoginRequest request
    ) {

        String identifier =
                request.getIdentifier().trim();

        User user =
                userRepository
                        .findByCustomerNumberOrNationalId(
                                identifier,
                                identifier
                        )
                        .orElseThrow(
                                () ->
                                        new InvalidCredentialsException(
                                                "Müşteri no / T.C. kimlik no veya şifre hatalı"
                                        )
                        );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "Müşteri no / T.C. kimlik no veya şifre hatalı"
            );
        }

        // JWT'nin iç kimliği şimdilik email olarak kalıyor.
        // Böylece accounts / transactions tarafını kırmıyoruz.
        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken.getToken(),
                UserMapper.toResponse(user)
        );
    }

    public AuthResponse register(
            CreateUserRequest request
    ) {

        User user =
                UserMapper.toEntity(request);

        user.setRole(Role.USER);

        User savedUser =
                userService.saveUser(user);

        String token =
                jwtService.generateToken(
                        savedUser.getEmail(),
                        savedUser.getRole()
                );

        RefreshToken refreshToken =
                refreshTokenService
                        .createRefreshToken(savedUser);

        return new AuthResponse(
                token,
                refreshToken.getToken(),
                UserMapper.toResponse(savedUser)
        );
    }

    @Transactional
    public AuthResponse refresh(
            RefreshTokenRequest request
    ) {

        RefreshToken oldRefreshToken =
                refreshTokenService
                        .validateRefreshToken(
                                request.getRefreshToken()
                        );

        User user =
                oldRefreshToken.getUser();

        refreshTokenService
                .deleteRefreshToken(
                        oldRefreshToken
                );

        String newAccessToken =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole()
                );

        RefreshToken newRefreshToken =
                refreshTokenService
                        .createRefreshToken(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                UserMapper.toResponse(user)
        );
    }
}