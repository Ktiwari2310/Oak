package com.oak.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pull_requests")
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id",  nullable = false, unique = true)
    public Issues issues;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_repo_id", nullable = false)
    public Repository headRepo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_repo_id", nullable = false)
    public Repository baseRepo;

    @Column(name = "head_branch",nullable = false, length = 255)
    private String head_branch;

    @Column(name = "base_branch",nullable = false, length = 255)
    private String base_branch;

    @Column(name = "merge_branch_sha",nullable = false, length = 40)
    private String merge_branch_sha;

    @Column(name ="is_merged" ,nullable = false)
    private boolean is_merged;

    @Column(name = "merged_at")
    private LocalDateTime merged_at;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Issues getIssues() {
        return issues;
    }
    public void setIssues(Issues issues) {
        this.issues = issues;
    }

    public Repository getHeadRepo() {
        return headRepo;
    }
    public void setHeadRepo(Repository headRepo) {
        this.headRepo = headRepo;
    }

    public Repository getBaseRepo() {
        return baseRepo;
    }
    public void setBaseRepo(Repository baseRepo) {
        this.baseRepo = baseRepo;
    }

    public String getHead_branch() {
        return head_branch;
    }
    public void setHead_branch(String head_branch) {
        this.head_branch = head_branch;
    }

    public String getBase_branch() {
        return base_branch;
    }
    public void setBase_branch(String base_branch) {
        this.base_branch = base_branch;
    }

    public String getMerge_branch_sha() {
        return merge_branch_sha;
    }
    public void setMerge_branch_sha(String merge_branch_sha) {
        this.merge_branch_sha = merge_branch_sha;
    }

    public boolean isIs_merged() {
        return is_merged;
    }
    public void setIs_merged(boolean is_merged) {
        this.is_merged = is_merged;
    }

    public LocalDateTime getMerged_at() {
        return merged_at;
    }
    public void setMerged_at(LocalDateTime merged_at) {
        this.merged_at = merged_at;
    }

}
