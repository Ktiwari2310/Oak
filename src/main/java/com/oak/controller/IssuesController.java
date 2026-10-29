package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.repository.IssuesRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import com.oak.response.ApiResponse;
import com.oak.response.IssuesResponse;
import com.oak.request.CreateIssueRequest;
import com.oak.request.UpdateIssueRequest;
import com.oak.exception.ResourceNotFoundException;
import com.oak.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssuesController {

    private static final Logger logger = LoggerFactory.getLogger(IssuesController.class);

    private final IssuesRepository issuesRepository;
    private final UserRepository userRepository;
    private final RepositoryRepository repositoryRepository;

    public IssuesController(
            IssuesRepository issuesRepository,
            UserRepository userRepository,
            RepositoryRepository repositoryRepository) {
        this.issuesRepository = issuesRepository;
        this.userRepository = userRepository;
        this.repositoryRepository = repositoryRepository;
    }

    @GetMapping
    // TODO(security): hide issues belonging to private repositories from non-owners.
    public ApiResponse<Page<IssuesResponse>> getAllIssues(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Issues> issues = issuesRepository.findAll(pageable);
        logger.info("Issues retrieved successfully: count={}", issues.getNumberOfElements());
        return ApiResponse.success("Issues retrieved successfully",
                issues.map(IssuesResponse::from));
    }

    @GetMapping("/{id}")
    // TODO(security): verify access to the issue's repository before returning it.
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueById(@PathVariable Integer id) {
        Issues issue = issuesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
        logger.info("Issue retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", IssuesResponse.from(issue)));
    }

    @GetMapping("/repository/{repoId}")
    // TODO(security): require repository ownership when the repository is private.
    public ApiResponse<List<IssuesResponse>> getIssuesByRepository(@PathVariable Long repoId) {
        List<Issues> issues = issuesRepository.findByRepositoryId(repoId);
        logger.info("Issues retrieved successfully for repository: repositoryId={}, count={}", repoId, issues.size());
        return ApiResponse.success("Issues retrieved successfully",
                issues.stream().map(IssuesResponse::from).toList());
    }

    @GetMapping("/repository/{repoId}/number/{issueNumber}")
    // TODO(security): require repository ownership when the repository is private.
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueByRepoAndNumber(
            @PathVariable Long repoId,
            @PathVariable Integer issueNumber) {

        Issues issue = issuesRepository.findByRepositoryIdAndIssueNumber(repoId, issueNumber)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issue not found for repository " + repoId + " and number " + issueNumber));
        logger.info("Issue retrieved successfully: repositoryId={}, issueNumber={}", repoId, issueNumber);
        return ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", IssuesResponse.from(issue)));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<IssuesResponse>> createIssue(@Valid @RequestBody CreateIssueRequest request) {
        User poster = userRepository.findById(request.posterId()).orElse(null);
        Repository repository = repositoryRepository.findByIdForUpdate(request.repositoryId()).orElse(null);

        if (poster == null || repository == null) {
            throw new BadRequestException("Poster or repository was not found");
        }

        Issues issue = new Issues();
        issue.setTitle(request.title());
        issue.setContent(request.content());
        issue.setIssueNumber(issuesRepository.findMaxIssueNumberByRepositoryId(repository.getId()) + 1);
        issue.setIsClosed(request.isClosed());
        issue.setId(null);
        issue.setPoster(poster);
        issue.setRepository(repository);

        if (issue.getIsClosed() == null) {
            issue.setIsClosed(false);
        }

        Issues savedIssue = issuesRepository.save(issue);
        logger.info("Issue created successfully: id={}", savedIssue.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Issue created successfully", IssuesResponse.from(savedIssue)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesResponse>> updateIssue(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateIssueRequest issue) {
        Issues existing = issuesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));

        if (issue.title() != null) {
            existing.setTitle(issue.title());
        }
        if (issue.content() != null) {
            existing.setContent(issue.content());
        }
        if (issue.isClosed() != null) {
            existing.setIsClosed(issue.isClosed());
        }

        Issues updatedIssue = issuesRepository.save(existing);
        logger.info("Issue updated successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue updated successfully", IssuesResponse.from(updatedIssue)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<IssuesResponse>> closeIssue(@PathVariable Integer id) {
        Issues existing = issuesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
        existing.setIsClosed(true);
        Issues closedIssue = issuesRepository.save(existing);
        logger.info("Issue closed successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue closed successfully", IssuesResponse.from(closedIssue)));
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<IssuesResponse>> reopenIssue(@PathVariable Integer id) {
        Issues existing = issuesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + id));
        existing.setIsClosed(false);
        Issues reopenedIssue = issuesRepository.save(existing);
        logger.info("Issue reopened successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue reopened successfully", IssuesResponse.from(reopenedIssue)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Integer id) {
        if (!issuesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Issue not found: " + id);
        }
        issuesRepository.deleteById(id);
        logger.info("Issue deleted successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue deleted successfully", null));
    }
}
