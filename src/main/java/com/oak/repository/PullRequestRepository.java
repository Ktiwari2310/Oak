package com.oak.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oak.entity.PullRequest;

public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByIssuesId(Integer issueId);

    List<PullRequest> findByHeadRepoId(Long headRepoId);

    List<PullRequest> findByBaseRepoId(Long baseRepoId);

    List<PullRequest> findByIsMerged(boolean isMerged);
}