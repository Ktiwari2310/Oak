package com.oak.controller;

import com.oak.entity.PullRequest;
import com.oak.repository.PullRequestRepository;
import com.oak.response.PullRequestResponse;
import com.oak.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pull-requests")
public class PullRequestController {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestController.class);

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @GetMapping
    public ApiResponse<List<PullRequestResponse>> getAllPullRequests() {
        List<PullRequest> pullRequests = pullRequestRepository.findAll();
        logger.info("Pull requests retrieved successfully: count={}", pullRequests.size());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.stream().map(PullRequestResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestById(@PathVariable Long id) {
        return pullRequestRepository.findById(id)
                .map(PullRequestResponse::from)
                .map(response -> {
                    logger.info("Pull request retrieved successfully: id={}", id);
                    return ResponseEntity.ok(ApiResponse.success("Pull request retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Pull request retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestByIssue(@PathVariable Integer issueId) {
        return pullRequestRepository.findByIssuesId(issueId)
                .map(PullRequestResponse::from)
                .map(response -> {
                    logger.info("Pull request retrieved successfully for issue: issueId={}", issueId);
                    return ResponseEntity.ok(ApiResponse.success("Pull request retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Pull request retrieval failed: issueId={} was not found", issueId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/head-repo/{repoId}")
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByHeadRepo(@PathVariable Long repoId) {
        List<PullRequest> pullRequests = pullRequestRepository.findByHeadRepoId(repoId);
        logger.info("Pull requests retrieved successfully for head repository: repositoryId={}, count={}",
                repoId, pullRequests.size());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.stream().map(PullRequestResponse::from).toList());
    }

    @GetMapping("/base-repo/{repoId}")
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByBaseRepo(@PathVariable Long repoId) {
        List<PullRequest> pullRequests = pullRequestRepository.findByBaseRepoId(repoId);
        logger.info("Pull requests retrieved successfully for base repository: repositoryId={}, count={}",
                repoId, pullRequests.size());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.stream().map(PullRequestResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<PullRequestResponse> createPullRequest(@RequestBody PullRequest pullRequest) {
        PullRequest savedPullRequest = pullRequestRepository.save(pullRequest);
        logger.info("Pull request created successfully: id={}", savedPullRequest.getId());
        return ApiResponse.success("Pull request created successfully",
                PullRequestResponse.from(savedPullRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> updatePullRequest(@PathVariable Long id, @RequestBody PullRequest pullRequest) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setHead_branch(pullRequest.getHead_branch());
            existing.setBase_branch(pullRequest.getBase_branch());
            existing.setMerge_branch_sha(pullRequest.getMerge_branch_sha());
            PullRequest updatedPullRequest = pullRequestRepository.save(existing);
            logger.info("Pull request updated successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Pull request updated successfully",
                    PullRequestResponse.from(updatedPullRequest)));
        }).orElseGet(() -> {
            logger.warn("Pull request update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PatchMapping("/{id}/merge")
    public ResponseEntity<ApiResponse<PullRequestResponse>> mergePullRequest(@PathVariable Long id) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setIsMerged(true);
            existing.setMerged_at(LocalDateTime.now());
            PullRequest mergedPullRequest = pullRequestRepository.save(existing);
            logger.info("Pull request merged successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Pull request merged successfully",
                    PullRequestResponse.from(mergedPullRequest)));
        }).orElseGet(() -> {
            logger.warn("Pull request merge failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePullRequest(@PathVariable Long id) {
        if (pullRequestRepository.existsById(id)) {
            pullRequestRepository.deleteById(id);
            logger.info("Pull request deleted successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Pull request deleted successfully", null));
        }
        logger.warn("Pull request deletion failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }
}