package com.oak.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;

public record UpdateRepositoryRequest(
        Long ownerId,
        @Size(max = 100) String name,
        String description,
        @JsonAlias("is_private") Boolean isPrivate,
        @JsonAlias("is_mirror") Boolean isMirror,
        @JsonAlias("clone_url") @Size(max = 255) String cloneUrl) {
}
