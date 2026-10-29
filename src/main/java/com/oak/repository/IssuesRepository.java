package com.oak.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.oak.entity.Issues;

public interface IssuesRepository extends JpaRepository<Issues, Integer> {

    List<Issues> findByRepositoryId(Long repositoryId);

    List<Issues> findByRepositoryIdAndIssueNumber(Long repositoryId, Integer issueNumber);

    @Query("select coalesce(max(issue.issueNumber), 0) from Issues issue where issue.repository.id = :repositoryId")
    Integer findMaxIssueNumberByRepositoryId(@Param("repositoryId") Long repositoryId);
}
