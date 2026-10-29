package com.oak.response;

import com.oak.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        boolean isAdmin,
        LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFull_name(),
                user.getEmail(),
                user.isIs_admin(),
                user.getCreated_at());
    }
}
