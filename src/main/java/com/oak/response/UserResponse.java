package com.oak.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oak.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        @JsonProperty("full_name") String fullName,
        String email,
        @JsonProperty("is_admin") boolean admin,
        @JsonProperty("created_at") LocalDateTime createdAt) {

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
