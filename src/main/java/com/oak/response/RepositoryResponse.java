package com.oak.response;

import com.oak.entity.Repository;

import java.time.LocalDateTime;

public record RepositoryResponse(
        Long id,
        Long ownerId,
        String name,
        String description,
        Boolean isPrivate,
        Boolean isMirror,
        String cloneUrl,
        LocalDateTime createdAt) {

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
