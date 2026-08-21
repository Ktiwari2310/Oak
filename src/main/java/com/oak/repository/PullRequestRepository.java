package com.oak.repository;

import com.oak.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    Optional<PullRequest> findByIssuesId(Integer issueId);

    List<PullRequest> findByHeadRepoId(Integer headRepoId);

    List<PullRequest> findByBaseRepoId(Integer baseRepoId);

    List<PullRequest> findByIsMerged(boolean isMerged);
}