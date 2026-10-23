package com.oak.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "labels")
public class Labels{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // many labels -> one repository (owns FK: repo_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 7)
    private String color;

    @Column(columnDefinition = "text")
    private String description;

    // one label appears on many issue_labels join rows (issue_labels.label_id -> labels.id)
    @OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IssuesLabels> issueLabels = new ArrayList<>();

    public Labels() {}

    // ---- Getters / Setters ----

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Repository getRepository() { return repository; }
    public void setRepository(Repository repository) { this.repository = repository; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<IssuesLabels> getIssueLabels() { return issueLabels; }
    public void setIssueLabels(List<IssuesLabels> issueLabels) { this.issueLabels = issueLabels; }
}
