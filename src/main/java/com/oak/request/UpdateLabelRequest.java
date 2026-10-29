package com.oak.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateLabelRequest(
        @Size(max = 100) String name,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color,
        String description) {
}
