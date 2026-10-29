package com.oak.request;

import jakarta.validation.constraints.Size;

public record UpdateIssueRequest(
        @Size(max = 255) String title,
        String content,
        Boolean isClosed) {
}
