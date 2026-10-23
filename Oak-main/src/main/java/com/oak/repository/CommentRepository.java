package com.oak.repository;

import com.oak.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssueId(Integer issueId);

    List<Comment> findByPosterId(Long posterId);
}