package com.oak.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oak.entity.Issues;

public interface IssuesRepository extends JpaRepository<Issues, Integer> {

    List<Issues> findByRepositoryId(Integer repositoryId);

    List<Issues> findByUserId(Long userId);

    List<Issues> findByIsClosed(Boolean isClosed);

    List<Issues> findByRepositoryIdAndIssueNumber(Integer repositoryId, Integer issueNumber);
}