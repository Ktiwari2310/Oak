package com.oak.entity;

import jakarta.persistence.*;

// Explicit join entity for the Issue <-> Label many-to-many relationship.
// Modeled as its own @Entity (rather than a plain @ManyToMany + @JoinTable)
// because issue_labels has its own surrogate primary key "id" in the schema.
@Entity
@Table(name = "issue_labels")
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
}