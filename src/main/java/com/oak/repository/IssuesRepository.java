package com.oak.repository;

import com.oak.entity.Issues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuesRepository extends JpaRepository<Issues, Integer> {

    List<Issues> findByRepositoryId(Integer repositoryId);

    List<Issues> findByPosterId(Long userId);

    List<Issues> findByIsClosed(Boolean isClosed);

    List<Issues> findByRepositoryIdAndIssueNumber(Integer repositoryId, Integer issueNumber);
}