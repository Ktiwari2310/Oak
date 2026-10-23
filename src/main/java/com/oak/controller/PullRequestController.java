package com.oak.controller;

import com.oak.entity.PullRequest;
import com.oak.repository.PullRequestRepository;
import com.oak.response.PullRequestResponse;
import com.oak.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pull-requests")
public class PullRequestController {

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @GetMapping
    public ApiResponse<List<PullRequestResponse>> getAllPullRequests() {
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequestRepository.findAll().stream().map(PullRequestResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestById(@PathVariable Long id) {
        return pullRequestRepository.findById(id)
                .map(PullRequestResponse::from)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Pull request retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestByIssue(@PathVariable Integer issueId) {
        return pullRequestRepository.findByIssuesId(issueId)
                .map(PullRequestResponse::from)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Pull request retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/head-repo/{repoId}")
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByHeadRepo(@PathVariable Long repoId) {
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequestRepository.findByHeadRepoId(repoId).stream().map(PullRequestResponse::from).toList());
    }

    @GetMapping("/base-repo/{repoId}")
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByBaseRepo(@PathVariable Long repoId) {
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequestRepository.findByBaseRepoId(repoId).stream().map(PullRequestResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<PullRequestResponse> createPullRequest(@RequestBody PullRequest pullRequest) {
        return ApiResponse.success("Pull request created successfully",
                PullRequestResponse.from(pullRequestRepository.save(pullRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> updatePullRequest(@PathVariable Long id, @RequestBody PullRequest pullRequest) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setHead_branch(pullRequest.getHead_branch());
            existing.setBase_branch(pullRequest.getBase_branch());
            existing.setMerge_branch_sha(pullRequest.getMerge_branch_sha());
            return ResponseEntity.ok(ApiResponse.success("Pull request updated successfully",
                    PullRequestResponse.from(pullRequestRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/merge")
    public ResponseEntity<ApiResponse<PullRequestResponse>> mergePullRequest(@PathVariable Long id) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setIsMerged(true);
            existing.setMerged_at(LocalDateTime.now());
            return ResponseEntity.ok(ApiResponse.success("Pull request merged successfully",
                    PullRequestResponse.from(pullRequestRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePullRequest(@PathVariable Long id) {
        if (pullRequestRepository.existsById(id)) {
            pullRequestRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Pull request deleted successfully", null));
        }
        return ResponseEntity.notFound().build();
    }
}