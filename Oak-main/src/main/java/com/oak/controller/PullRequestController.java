package com.oak.controller;

import com.oak.entity.PullRequest;
import com.oak.repository.PullRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pull-requests")
public class PullRequestController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(PullRequestController.class);

    @Autowired
    private PullRequestRepository pullRequestRepository;

    // Get all pull requests
    @GetMapping
    public List<PullRequest> getAllPullRequests() {

        logger.info("Fetching all pull requests");

        List<PullRequest> pullRequests = pullRequestRepository.findAll();

        logger.info("Successfully fetched {} pull requests", pullRequests.size());

        return pullRequests;
    }

    // Get pull request by ID
    @GetMapping("/{id}")
    public ResponseEntity<PullRequest> getPullRequestById(@PathVariable Long id) {

        logger.info("Fetching pull request with ID: {}", id);

        return pullRequestRepository.findById(id)
                .map(pullRequest -> {
                    logger.info("Pull request found with ID: {}", id);
                    return ResponseEntity.ok(pullRequest);
                })
                .orElseGet(() -> {
                    logger.warn("Pull request not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get pull request by issue ID
    @GetMapping("/issue/{issueId}")
    public ResponseEntity<PullRequest> getPullRequestByIssue(
            @PathVariable Integer issueId) {

        logger.info("Fetching pull request for issue ID: {}", issueId);

        return pullRequestRepository.findByIssuesId(issueId)
                .map(pullRequest -> {
                    logger.info("Pull request found for issue ID: {}", issueId);
                    return ResponseEntity.ok(pullRequest);
                })
                .orElseGet(() -> {
                    logger.warn("No pull request found for issue ID: {}", issueId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get pull requests by head repository
    @GetMapping("/head-repo/{repoId}")
    public List<PullRequest> getPullRequestsByHeadRepo(@PathVariable Integer repoId) {

        logger.info("Fetching pull requests for head repository ID: {}", repoId);

        List<PullRequest> pullRequests =
                pullRequestRepository.findByHeadRepoId(repoId);

        logger.info("Found {} pull requests for head repository ID: {}",
                pullRequests.size(), repoId);

        return pullRequests;
    }

    // Get pull requests by base repository
    @GetMapping("/base-repo/{repoId}")
    public List<PullRequest> getPullRequestsByBaseRepo(@PathVariable Integer repoId) {

        logger.info("Fetching pull requests for base repository ID: {}", repoId);

        List<PullRequest> pullRequests =
                pullRequestRepository.findByBaseRepoId(repoId);

        logger.info("Found {} pull requests for base repository ID: {}",
                pullRequests.size(), repoId);

        return pullRequests;
    }

    // Create pull request
    @PostMapping
    public PullRequest createPullRequest(@RequestBody PullRequest pullRequest) {

        logger.info("Creating a new pull request");

        PullRequest savedPullRequest = pullRequestRepository.save(pullRequest);

        logger.info("Pull request created successfully with ID: {}",
                savedPullRequest.getId());

        return savedPullRequest;
    }

    // Update pull request
    @PutMapping("/{id}")
    public ResponseEntity<PullRequest> updatePullRequest(
            @PathVariable Long id,
            @RequestBody PullRequest pullRequest) {

        logger.info("Updating pull request with ID: {}", id);

        return pullRequestRepository.findById(id).map(existing -> {
            existing.setHeadBranch(pullRequest.getHead_branch());
            existing.setBaseBranch(pullRequest.getBase_branch());
            existing.setMergeBranchSha(pullRequest.getMerge_branch_sha());

            PullRequest updated = pullRequestRepository.save(existing);

            logger.info("Pull request updated successfully with ID: {}", id);

            return ResponseEntity.ok(updated);
        }).orElseGet(() -> {
            logger.warn("Cannot update. Pull request not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Merge pull request
    @PatchMapping("/{id}/merge")
    public ResponseEntity<PullRequest> mergePullRequest(@PathVariable Long id) {

        logger.info("Merging pull request with ID: {}", id);

        return pullRequestRepository.findById(id).map(existing -> {
            existing.setIs_merged(true);
            existing.setMerged_at(LocalDateTime.now());

            PullRequest merged = pullRequestRepository.save(existing);

            logger.info("Pull request merged successfully with ID: {}", id);

            return ResponseEntity.ok(merged);
        }).orElseGet(() -> {
            logger.warn("Cannot merge. Pull request not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Delete pull request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePullRequest(@PathVariable Long id) {

        logger.info("Deleting pull request with ID: {}", id);

        if (pullRequestRepository.existsById(id)) {
            pullRequestRepository.deleteById(id);

            logger.info("Pull request deleted successfully with ID: {}", id);

            return ResponseEntity.noContent().build();
        }

        logger.warn("Cannot delete. Pull request not found with ID: {}", id);

        return ResponseEntity.notFound().build();
    }
}
