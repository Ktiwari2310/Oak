package com.oak.response;

import com.oak.entity.PullRequest;

import java.time.LocalDateTime;

public record PullRequestResponse(
        Long id,
        Integer issueId,
        Long headRepoId,
        Long baseRepoId,
        String headBranch,
        String baseBranch,
        String mergeBranchSha,
        boolean isMerged,
        LocalDateTime mergedAt) {

    public static PullRequestResponse from(PullRequest pullRequest) {
        return new PullRequestResponse(
                pullRequest.getId(),
                pullRequest.getIssues() == null ? null : pullRequest.getIssues().getId(),
                pullRequest.getHeadRepo() == null ? null : pullRequest.getHeadRepo().getId(),
                pullRequest.getBaseRepo() == null ? null : pullRequest.getBaseRepo().getId(),
                pullRequest.getHeadBranch(),
                pullRequest.getBaseBranch(),
                pullRequest.getMergeBranchSha(),
                pullRequest.isMerged(),
                pullRequest.getMergedAt());
    }
}
