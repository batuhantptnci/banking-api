package com.batuhan.bankingapi.controller;

import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.UpdateUserRequest;
import com.batuhan.bankingapi.dto.UserResponse;
import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.mapper.UserMapper;
import com.batuhan.bankingapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Tüm kullanıcıları getir
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    // ID'ye göre kullanıcı getir
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return UserMapper.toResponse(user);
    }

    // Yeni kullanıcı oluştur
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        User user = UserMapper.toEntity(request);

        User savedUser = userService.saveUser(user);

        return ResponseEntity
                .status(201)
                .body(UserMapper.toResponse(savedUser));
    }

    // Kullanıcı güncelle
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User user = UserMapper.toEntity(request);

        User updatedUser = userService.updateUser(id, user);

        return UserMapper.toResponse(updatedUser);
    }

    // Kullanıcı sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}