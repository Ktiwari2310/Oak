package com.oak.response;

import com.oak.entity.Issues;
import java.time.LocalDateTime;

public class IssuesResponse {

    private Integer id;
    private Long repositoryId;
    private Long userId;
    private Integer issueNumber;
    private String title;
    private String content;
    private Boolean isClosed;
    private LocalDateTime createdAt;

    public IssuesResponse() {
    }

    public IssuesResponse(Integer id, Long repositoryId, Long userId, Integer issueNumber,
                          String title, String content, Boolean isClosed, LocalDateTime createdAt) {
        this.id = id;
        this.repositoryId = repositoryId;
        this.userId = userId;
        this.issueNumber = issueNumber;
        this.title = title;
        this.content = content;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
    }

    public static IssuesResponse from(Issues issue) {
        return new IssuesResponse(
                issue.getId(),
                issue.getRepository() != null ? issue.getRepository().getId() : null,
                issue.getPoster() != null ? issue.getPoster().getId() : null,
                issue.getIssueNumber(),
                issue.getTitle(),
                issue.getContent(),
                issue.getIsClosed(),
                issue.getCreatedAt()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(Integer issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
