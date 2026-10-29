package com.oak.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 100) String username,
        @JsonAlias("full_name") @Size(max = 255) String fullName,
        @Email @Size(max = 255) String email,
        @Size(min = 8, max = 72) String password) {
}
