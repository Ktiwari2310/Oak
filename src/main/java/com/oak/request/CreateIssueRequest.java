package com.oak.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateIssueRequest(
        @NotNull Long repositoryId,
        @NotNull Long posterId,
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        Boolean isClosed) {
}
