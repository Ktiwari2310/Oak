package com.oak.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "issues", uniqueConstraints = {
        @UniqueConstraint(name = "uk_issues_repo_index", columnNames = {"repo_id", "`index`"})
})
public class Issues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;

    @Column(name = "`index`", nullable = false)
    private Integer issueNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<IssuesLabels> issueLabels = new ArrayList<>();

    @OneToOne(mappedBy = "issues", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private PullRequest pullRequest;

    public Issues() {
    }

    public Issues(Integer id, Repository repository, User poster,
                  Integer issueNumber, String title, String content,
                  Boolean isClosed, LocalDateTime createdAt) {
        this.id = id;
        this.repository = repository;
        this.poster = poster;
        this.issueNumber = issueNumber;
        this.title = title;
        this.content = content;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public User getPoster() {
        return poster;
    }
    public void setPoster(User poster) {
        this.poster = poster;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<IssuesLabels> getIssueLabels() {
        return issueLabels;
    }

    public void setIssueLabels(List<IssuesLabels> issueLabels) {
        this.issueLabels = issueLabels;
    }

    public PullRequest getPullRequest() {
        return pullRequest;
    }

    public void setPullRequest(PullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }
}
