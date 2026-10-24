package com.oak.response;

import com.oak.entity.IssuesLabels;

public record IssuesLabelsResponse(
        Integer id,
        Integer issueId,
        Integer labelId) {

    public static IssuesLabelsResponse from(IssuesLabels issuesLabel) {
        return new IssuesLabelsResponse(
                issuesLabel.getId(),
                issuesLabel.getIssue() == null ? null : issuesLabel.getIssue().getId(),
                issuesLabel.getLabel() == null ? null : issuesLabel.getLabel().getId());
    }
}
