package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.repository.IssuesRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import com.oak.response.ApiResponse;
import com.oak.response.IssuesResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssuesController {

    private static final Logger logger = LoggerFactory.getLogger(IssuesController.class);

    @Autowired
    private IssuesRepository issuesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @GetMapping
    public ApiResponse<List<IssuesResponse>> getAllIssues() {
        List<Issues> issues = issuesRepository.findAll();
        logger.info("Issues retrieved successfully: count={}", issues.size());
        return ApiResponse.success("Issues retrieved successfully",
                issues.stream().map(IssuesResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueById(@PathVariable Integer id) {
        return issuesRepository.findById(id)
                .map(IssuesResponse::from)
                .map(response -> {
                    logger.info("Issue retrieved successfully: id={}", id);
                    return ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Issue retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/repository/{repoId}")
    public ApiResponse<List<IssuesResponse>> getIssuesByRepository(@PathVariable Integer repoId) {
        List<Issues> issues = issuesRepository.findByRepositoryId(repoId);
        logger.info("Issues retrieved successfully for repository: repositoryId={}, count={}", repoId, issues.size());
        return ApiResponse.success("Issues retrieved successfully",
                issues.stream().map(IssuesResponse::from).toList());
    }

    @GetMapping("/repository/{repoId}/number/{issueNumber}")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueByRepoAndNumber(
            @PathVariable Integer repoId,
            @PathVariable Integer issueNumber) {

        return issuesRepository.findByRepositoryIdAndIssueNumber(repoId, issueNumber)
                .stream()
                .findFirst()
                .map(IssuesResponse::from)
                .map(response -> {
                    logger.info("Issue retrieved successfully: repositoryId={}, issueNumber={}", repoId, issueNumber);
                    return ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Issue retrieval failed: repositoryId={}, issueNumber={} was not found", repoId, issueNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssuesResponse>> createIssue(@RequestBody Issues issue) {
        if (issue.getPoster() == null || issue.getPoster().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (issue.getRepository() == null || issue.getRepository().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        User poster = userRepository.findById(issue.getPoster().getId()).orElse(null);
        Repository repository = repositoryRepository.findById(issue.getRepository().getId()).orElse(null);

        if (poster == null || repository == null) {
            return ResponseEntity.badRequest().build();
        }

        issue.setId(null);
        issue.setPoster(poster);
        issue.setRepository(repository);

        if (issue.getCreatedAt() == null) {
            issue.setCreatedAt(LocalDateTime.now());
        }

        if (issue.getIsClosed() == null) {
            issue.setIsClosed(false);
        }

        Issues savedIssue = issuesRepository.save(issue);
        logger.info("Issue created successfully: id={}", savedIssue.getId());

        return ResponseEntity.ok(ApiResponse.success("Issue created successfully", IssuesResponse.from(savedIssue)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesResponse>> updateIssue(@PathVariable Integer id, @RequestBody Issues issue) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setTitle(issue.getTitle());
            existing.setContent(issue.getContent());
            existing.setIsClosed(issue.getIsClosed());
            existing.setIssueNumber(issue.getIssueNumber());

            if (issue.getPoster() != null && issue.getPoster().getId() != null) {
                userRepository.findById(issue.getPoster().getId())
                        .ifPresent(existing::setPoster);
            }

            if (issue.getRepository() != null && issue.getRepository().getId() != null) {
                repositoryRepository.findById(issue.getRepository().getId())
                        .ifPresent(existing::setRepository);
            }

            Issues updatedIssue = issuesRepository.save(existing);
            logger.info("Issue updated successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Issue updated successfully", IssuesResponse.from(updatedIssue)));
        }).orElseGet(() -> {
            logger.warn("Issue update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<IssuesResponse>> closeIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(true);
            Issues closedIssue = issuesRepository.save(existing);
            logger.info("Issue closed successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Issue closed successfully", IssuesResponse.from(closedIssue)));
        }).orElseGet(() -> {
            logger.warn("Issue close failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<IssuesResponse>> reopenIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(false);
            Issues reopenedIssue = issuesRepository.save(existing);
            logger.info("Issue reopened successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Issue reopened successfully", IssuesResponse.from(reopenedIssue)));
        }).orElseGet(() -> {
            logger.warn("Issue reopen failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Integer id) {
        if (issuesRepository.existsById(id)) {
            issuesRepository.deleteById(id);
            logger.info("Issue deleted successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Issue deleted successfully", null));
        }

        logger.warn("Issue deletion failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }
}
