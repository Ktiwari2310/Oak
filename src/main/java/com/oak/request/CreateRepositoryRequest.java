package com.oak.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRepositoryRequest(
        @NotNull Long ownerId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank String description,
        @JsonAlias("is_private") @NotNull Boolean isPrivate,
        @JsonAlias("is_mirror") @NotNull Boolean isMirror,
        @JsonAlias("clone_url") @NotBlank @Size(max = 255) String cloneUrl) {
}
