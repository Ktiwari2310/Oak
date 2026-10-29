package com.oak.request;

import com.oak.entity.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotNull Integer issueId,
        @NotNull Long posterId,
        @NotNull CommentType type,
        @NotBlank String content) {
}
