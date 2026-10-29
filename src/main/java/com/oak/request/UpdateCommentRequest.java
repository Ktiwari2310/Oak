package com.oak.request;

import com.oak.entity.CommentType;

public record UpdateCommentRequest(
        CommentType type,
        String content) {
}
