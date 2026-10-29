package com.oak.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oak.entity.PullRequest;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByIssuesId(Integer issueId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pullRequest from PullRequest pullRequest where pullRequest.id = :id")
    Optional<PullRequest> findByIdForUpdate(@Param("id") Long id);

    List<PullRequest> findByHeadRepoId(Long headRepoId);

    List<PullRequest> findByBaseRepoId(Long baseRepoId);

}