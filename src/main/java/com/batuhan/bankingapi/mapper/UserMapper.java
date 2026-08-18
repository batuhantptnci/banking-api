package com.batuhan.bankingapi.mapper;

import com.batuhan.bankingapi.dto.UserResponse;
import com.batuhan.bankingapi.dto.CreateUserRequest;
import com.batuhan.bankingapi.dto.UpdateUserRequest;
import com.batuhan.bankingapi.entity.User;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
    public static User toEntity(CreateUserRequest request) {
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return user;
    }
    public static User toEntity(UpdateUserRequest request) {
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        return user;
    }
}