package com.oak.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int issue_id;
    private int poster_id;
    private String type;
    private String content;
    private LocalDateTime created_at = LocalDateTime.now();

    public Comment(int issue_id, int poster_id, String type, String content) {
        this.issue_id = issue_id;
        this.poster_id = poster_id;
        this.type = type;
        this.content = content;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getIssue_id() {
        return issue_id;
    }
    public void setIssue_id(int issue_id) {
        this.issue_id = issue_id;
    }
    public int getPoster_id() {
        return poster_id;
    }
    public void setPoster_id(int poster_id) {
        this.poster_id = poster_id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
