package com.oak.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oak.entity.Repository;

import java.time.LocalDateTime;

public record RepositoryResponse(
        Long id,
        Long ownerId,
        String name,
        String description,
        @JsonProperty("is_private") Boolean isPrivate,
        @JsonProperty("is_mirror") Boolean isMirror,
        @JsonProperty("clone_url") String cloneUrl,
        @JsonProperty("created_at") LocalDateTime createdAt) {

    public static RepositoryResponse from(Repository repository) {
        return new RepositoryResponse(
                repository.getId(),
                repository.getOwner() == null ? null : repository.getOwner().getId(),
                repository.getName(),
                repository.getDescription(),
                repository.getIs_private(),
                repository.getIs_mirror(),
                repository.getClone_url(),
                repository.getCreated_at());
    }
}
