package com.oak.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oak.entity.PullRequest;

import java.time.LocalDateTime;

public record PullRequestResponse(
        Long id,
        Integer issueId,
        Long headRepoId,
        Long baseRepoId,
        @JsonProperty("head_branch") String headBranch,
        @JsonProperty("base_branch") String baseBranch,
        @JsonProperty("merge_branch_sha") String mergeBranchSha,
        boolean merged,
        @JsonProperty("merged_at") LocalDateTime mergedAt) {

    public static PullRequestResponse from(PullRequest pullRequest) {
        return new PullRequestResponse(
                pullRequest.getId(),
                pullRequest.getIssues() == null ? null : pullRequest.getIssues().getId(),
                pullRequest.getHeadRepo() == null ? null : pullRequest.getHeadRepo().getId(),
                pullRequest.getBaseRepo() == null ? null : pullRequest.getBaseRepo().getId(),
                pullRequest.getHead_branch(),
                pullRequest.getBase_branch(),
                pullRequest.getMerge_branch_sha(),
                pullRequest.getIsMerged(),
                pullRequest.getMerged_at());
    }
}
