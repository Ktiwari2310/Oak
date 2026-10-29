package com.oak.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Size;

public record UpdatePullRequestRequest(
        @JsonAlias("head_branch") @Size(max = 255) String headBranch,
        @JsonAlias("base_branch") @Size(max = 255) String baseBranch,
        @JsonAlias("merge_branch_sha") @Size(max = 40) String mergeBranchSha) {
}
