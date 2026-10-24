package com.oak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oak.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssueId(Integer issueId);

    List<Comment> findByPosterId(Long posterId);
}