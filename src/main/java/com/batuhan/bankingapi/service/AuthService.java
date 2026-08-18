package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.dto.AuthResponse;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.LoginRequest;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.InvalidCredentialsException;
import com.batuhan.bankingapi.mapper.UserMapper;
import com.batuhan.bankingapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Email veya şifre hatalı"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "Email veya şifre hatalı"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                UserMapper.toResponse(user)
        );
    }
    public AuthResponse register(CreateUserRequest request) {

        User user = UserMapper.toEntity(request);

        User savedUser = userService.saveUser(user);

        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                UserMapper.toResponse(savedUser)
        );
    }
}