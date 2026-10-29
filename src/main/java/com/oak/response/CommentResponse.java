package com.oak.response;

import com.oak.entity.Comment;
import com.oak.entity.CommentType;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Integer issueId,
        Long posterId,
        CommentType type,
        String content,
        LocalDateTime createdAt) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getIssue() == null ? null : comment.getIssue().getId(),
                comment.getPoster() == null ? null : comment.getPoster().getId(),
                comment.getType(),
                comment.getContent(),
                comment.getCreated_at());
    }
}
