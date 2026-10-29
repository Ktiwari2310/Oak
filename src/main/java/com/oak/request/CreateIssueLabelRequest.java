package com.oak.request;

import jakarta.validation.constraints.NotNull;

public record CreateIssueLabelRequest(
        @NotNull Integer issueId,
        @NotNull Integer labelId) {
}
