package com.oak.controller;

import java.util.List;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.oak.entity.PullRequest;
import com.oak.repository.IssuesRepository;
import com.oak.repository.PullRequestRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.response.ApiResponse;
import com.oak.response.PullRequestResponse;
import com.oak.request.CreatePullRequestRequest;
import com.oak.request.UpdatePullRequestRequest;
import com.oak.exception.ResourceNotFoundException;
import com.oak.exception.BadRequestException;
import com.oak.exception.ConflictException;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@RestController
@RequestMapping("/api/pull-requests")
public class PullRequestController {

    private static final Logger logger = LoggerFactory.getLogger(PullRequestController.class);

    private final PullRequestRepository pullRequestRepository;
    private final IssuesRepository issuesRepository;
    private final RepositoryRepository repositoryRepository;

    public PullRequestController(
            PullRequestRepository pullRequestRepository,
            IssuesRepository issuesRepository,
            RepositoryRepository repositoryRepository) {
        this.pullRequestRepository = pullRequestRepository;
        this.issuesRepository = issuesRepository;
        this.repositoryRepository = repositoryRepository;
    }

    @GetMapping
    // TODO(security): filter pull requests whose head or base repository is private.
    public ApiResponse<Page<PullRequestResponse>> getAllPullRequests(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<PullRequest> pullRequests = pullRequestRepository.findAll(pageable);
        logger.info("Pull requests retrieved successfully: count={}", pullRequests.getNumberOfElements());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.map(PullRequestResponse::from));
    }

    @GetMapping("/{id}")
    // TODO(security): verify caller access to private head/base repositories.
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestById(@PathVariable Long id) {
        PullRequest pullRequest = pullRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found: " + id));
        logger.info("Pull request retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success(
                "Pull request retrieved successfully", PullRequestResponse.from(pullRequest)));
    }

    @GetMapping("/issue/{issueId}")
    // TODO(security): verify caller access to the linked issue's repository.
    public ResponseEntity<ApiResponse<PullRequestResponse>> getPullRequestByIssue(@PathVariable Integer issueId) {
        PullRequest pullRequest = pullRequestRepository.findByIssuesId(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found for issue: " + issueId));
        logger.info("Pull request retrieved successfully for issue: issueId={}", issueId);
        return ResponseEntity.ok(ApiResponse.success(
                "Pull request retrieved successfully", PullRequestResponse.from(pullRequest)));
    }

    @GetMapping("/head-repo/{repoId}")
    // TODO(security): require repository ownership when the head repository is private.
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByHeadRepo(@PathVariable Long repoId) {
        List<PullRequest> pullRequests = pullRequestRepository.findByHeadRepoId(repoId);
        logger.info("Pull requests retrieved successfully for head repository: repositoryId={}, count={}",
                repoId, pullRequests.size());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.stream().map(PullRequestResponse::from).toList());
    }

    @GetMapping("/base-repo/{repoId}")
    // TODO(security): require repository ownership when the base repository is private.
    public ApiResponse<List<PullRequestResponse>> getPullRequestsByBaseRepo(@PathVariable Long repoId) {
        List<PullRequest> pullRequests = pullRequestRepository.findByBaseRepoId(repoId);
        logger.info("Pull requests retrieved successfully for base repository: repositoryId={}, count={}",
                repoId, pullRequests.size());
        return ApiResponse.success("Pull requests retrieved successfully",
                pullRequests.stream().map(PullRequestResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PullRequestResponse>> createPullRequest(
            @Valid @RequestBody CreatePullRequestRequest request) {
        var issue = issuesRepository.findById(request.issueId()).orElse(null);
        var headRepo = repositoryRepository.findById(request.headRepoId()).orElse(null);
        var baseRepo = repositoryRepository.findById(request.baseRepoId()).orElse(null);
        if (issue == null || headRepo == null || baseRepo == null) {
            throw new BadRequestException("Issue or repository was not found");
        }
        if (Objects.equals(headRepo.getId(), baseRepo.getId())
                && Objects.equals(request.headBranch(), request.baseBranch())) {
            throw new BadRequestException("Head and base repository and branch must differ");
        }

        PullRequest pullRequest = new PullRequest();
        pullRequest.setHeadBranch(request.headBranch());
        pullRequest.setBaseBranch(request.baseBranch());
        pullRequest.setMergeBranchSha(request.mergeBranchSha());
        pullRequest.setId(null);
        pullRequest.setIssues(issue);
        pullRequest.setHeadRepo(headRepo);
        pullRequest.setBaseRepo(baseRepo);
        PullRequest savedPullRequest = pullRequestRepository.save(pullRequest);
        logger.info("Pull request created successfully: id={}", savedPullRequest.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pull request created successfully", PullRequestResponse.from(savedPullRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PullRequestResponse>> updatePullRequest(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePullRequestRequest pullRequest) {
        PullRequest existing = pullRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found: " + id));
        if (pullRequest.headBranch() != null) {
            existing.setHeadBranch(pullRequest.headBranch());
        }
        if (pullRequest.baseBranch() != null) {
            existing.setBaseBranch(pullRequest.baseBranch());
        }
        if (pullRequest.mergeBranchSha() != null) {
            existing.setMergeBranchSha(pullRequest.mergeBranchSha());
        }
        PullRequest updatedPullRequest = pullRequestRepository.save(existing);
        logger.info("Pull request updated successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Pull request updated successfully",
                PullRequestResponse.from(updatedPullRequest)));
    }

    @PatchMapping("/{id}/merge")
    @Transactional
    public ResponseEntity<ApiResponse<PullRequestResponse>> mergePullRequest(@PathVariable Long id) {
        PullRequest existing = pullRequestRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pull request not found: " + id));
        if (existing.isMerged()) {
            throw new ConflictException("Pull request has already been merged");
        }
        existing.setMerged(true);
        existing.setMergedAt(LocalDateTime.now());
        existing.getIssues().setIsClosed(true);
        issuesRepository.save(existing.getIssues());
        PullRequest mergedPullRequest = pullRequestRepository.save(existing);
        logger.info("Pull request merged successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Pull request merged successfully",
                PullRequestResponse.from(mergedPullRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePullRequest(@PathVariable Long id) {
        if (!pullRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pull request not found: " + id);
        }
        pullRequestRepository.deleteById(id);
        logger.info("Pull request deleted successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Pull request deleted successfully", null));
    }
}