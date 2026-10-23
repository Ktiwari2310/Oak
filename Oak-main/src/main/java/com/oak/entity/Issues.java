package com.oak.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
public class Issues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @ManyToOne
    @JoinColumn(name = "poster_id", nullable = false)
    private User user;

    @Column(name = "index", nullable = false)
    private Integer issueNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Issues() {
    }

    public Issues(Integer id, Repository repository, User user,
                  Integer issueNumber, String title, String content,
                  Boolean isClosed, LocalDateTime createdAt) {
        this.id = id;
        this.repository = repository;
        this.user = user;
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

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
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

//    @ManyToOne
//    @JoinColumn(name = "milestone_id", nullable = false)
//    private Milestone milestone;
//
//    public Milestone getMilestone() {
//        return milestone;
//    }
//
//    public void setMilestone(Milestone milestone) {
//        this.milestone = milestone;
//    }
}
