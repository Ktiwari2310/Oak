package com.oak.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_labels", uniqueConstraints = {
        @UniqueConstraint(name = "uk_issue_labels_issue_label", columnNames = {"issue_id", "label_id"})
})
public class IssuesLabels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // many issue_labels rows -> one issue (owns FK: issue_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issues issue;

    // many issue_labels rows -> one label (owns FK: label_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", nullable = false)
    private Labels label;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public IssuesLabels() {}

    public IssuesLabels(Issues issue, Labels label) {
        this.issue = issue;
        this.label = label;
    }

    // ---- Getters / Setters ----

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Issues getIssue() { return issue; }
    public void setIssue(Issues issue) { this.issue = issue; }

    public Labels getLabel() { return label; }
    public void setLabel(Labels label) { this.label = label; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}