package com.oak.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePullRequestRequest(
        @NotNull Integer issueId,
        @NotNull Long headRepoId,
        @NotNull Long baseRepoId,
        @JsonAlias("head_branch") @NotBlank @Size(max = 255) String headBranch,
        @JsonAlias("base_branch") @NotBlank @Size(max = 255) String baseBranch,
        @JsonAlias("merge_branch_sha") @NotBlank @Size(max = 40) String mergeBranchSha) {
}
