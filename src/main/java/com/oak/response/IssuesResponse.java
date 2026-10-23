package com.oak.response;

import com.oak.entity.Issues;

import java.time.LocalDateTime;

public record IssuesResponse(
        Integer id,
        Long repositoryId,
        Long userId,
        Integer issueNumber,
        String title,
        String content,
        Boolean isClosed,
        LocalDateTime createdAt) {

    public static IssuesResponse from(Issues issue) {
        return new IssuesResponse(
                issue.getId(),
                issue.getRepository() == null ? null : issue.getRepository().getId(),
                issue.getUser() == null ? null : issue.getUser().getId(),
                issue.getIssueNumber(),
                issue.getTitle(),
                issue.getContent(),
                issue.getIsClosed(),
                issue.getCreatedAt());
    }
}
