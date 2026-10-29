package com.oak.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pull_requests")
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id",  nullable = false, unique = true)
    private Issues issues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_repo_id", nullable = false)
    private Repository headRepo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_repo_id", nullable = false)
    private Repository baseRepo;

    @Column(name = "head_branch", nullable = false, length = 255)
    private String headBranch;

    @Column(name = "base_branch", nullable = false, length = 255)
    private String baseBranch;

    @Column(name = "merge_branch_sha", nullable = false, length = 40)
    private String mergeBranchSha;

    @Column(name = "is_merged", nullable = false)
    private boolean isMerged;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Issues getIssues() { return issues; }
    public void setIssues(Issues issues) { this.issues = issues; }

    public Repository getHeadRepo() { return headRepo; }
    public void setHeadRepo(Repository headRepo) { this.headRepo = headRepo; }

    public Repository getBaseRepo() { return baseRepo; }
    public void setBaseRepo(Repository baseRepo) { this.baseRepo = baseRepo; }

    public String getHeadBranch() { return headBranch; }
    public void setHeadBranch(String headBranch) { this.headBranch = headBranch; }

    public String getBaseBranch() { return baseBranch; }
    public void setBaseBranch(String baseBranch) { this.baseBranch = baseBranch; }

    public String getMergeBranchSha() { return mergeBranchSha; }
    public void setMergeBranchSha(String mergeBranchSha) { this.mergeBranchSha = mergeBranchSha; }

    public boolean isMerged() { return isMerged; }
    public void setMerged(boolean merged) { isMerged = merged; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getMergedAt() { return mergedAt; }
    public void setMergedAt(LocalDateTime mergedAt) { this.mergedAt = mergedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
