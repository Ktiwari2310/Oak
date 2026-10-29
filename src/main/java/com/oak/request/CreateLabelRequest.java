package com.oak.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateLabelRequest(
        @NotNull Long repositoryId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color,
        String description) {
}
