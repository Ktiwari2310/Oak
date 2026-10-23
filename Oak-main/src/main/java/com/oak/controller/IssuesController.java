package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.repository.IssuesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssuesController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(IssuesController.class);

    @Autowired
    private IssuesRepository issuesRepository;

    // Get all issues
    @GetMapping
    public List<Issues> getAllIssues() {

        logger.info("Fetching all issues");

        List<Issues> issues = issuesRepository.findAll();

        logger.info("Successfully fetched {} issues", issues.size());

        return issues;
    }

    // Get issue by ID
    @GetMapping("/{id}")
    public ResponseEntity<Issues> getIssueById(@PathVariable Integer id) {

        logger.info("Fetching issue with ID: {}", id);

        return issuesRepository.findById(id)
                .map(issue -> {
                    logger.info("Issue found with ID: {}", id);
                    return ResponseEntity.ok(issue);
                })
                .orElseGet(() -> {
                    logger.warn("Issue not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get issues by repository
    @GetMapping("/repository/{repoId}")
    public List<Issues> getIssuesByRepository(@PathVariable Integer repoId) {

        logger.info("Fetching issues for repository ID: {}", repoId);

        List<Issues> issues = issuesRepository.findByRepositoryId(repoId);

        logger.info("Found {} issues for repository ID: {}",
                issues.size(), repoId);

        return issues;
    }

    // Get issue by repository ID and issue number
    @GetMapping("/repository/{repoId}/number/{issueNumber}")
    public ResponseEntity<Issues> getIssueByRepoAndNumber(
            @PathVariable Integer repoId,
            @PathVariable Integer issueNumber) {

        logger.info("Fetching issue number {} from repository ID: {}",
                issueNumber, repoId);

        return issuesRepository
                .findByRepositoryIdAndIssueNumber(repoId, issueNumber)
                .stream()
                .findFirst()
                .map(issue -> {
                    logger.info("Issue number {} found in repository ID: {}",
                            issueNumber, repoId);
                    return ResponseEntity.ok(issue);
                })
                .orElseGet(() -> {
                    logger.warn("Issue number {} not found in repository ID: {}",
                            issueNumber, repoId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Create issue
    @PostMapping
    public Issues createIssue(@RequestBody Issues issue) {

        logger.info("Creating a new issue");

        Issues savedIssue = issuesRepository.save(issue);

        logger.info("Issue created successfully");

        return savedIssue;
    }

    // Update issue
    @PutMapping("/{id}")
    public ResponseEntity<Issues> updateIssue(
            @PathVariable Integer id,
            @RequestBody Issues issue) {

        logger.info("Updating issue with ID: {}", id);

        return issuesRepository.findById(id).map(existing -> {
            existing.setTitle(issue.getTitle());
            existing.setContent(issue.getContent());
            existing.setIsClosed(issue.getIsClosed());
            existing.setUser(issue.getUser());
            existing.setRepository(issue.getRepository());
            existing.setIssueNumber(issue.getIssueNumber());

            Issues updatedIssue = issuesRepository.save(existing);

            logger.info("Issue updated successfully with ID: {}", id);

            return ResponseEntity.ok(updatedIssue);
        }).orElseGet(() -> {
            logger.warn("Cannot update. Issue not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Close issue
    @PatchMapping("/{id}/close")
    public ResponseEntity<Issues> closeIssue(@PathVariable Integer id) {

        logger.info("Closing issue with ID: {}", id);

        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(true);

            Issues closedIssue = issuesRepository.save(existing);

            logger.info("Issue closed successfully with ID: {}", id);

            return ResponseEntity.ok(closedIssue);
        }).orElseGet(() -> {
            logger.warn("Cannot close. Issue not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Reopen issue
    @PatchMapping("/{id}/reopen")
    public ResponseEntity<Issues> reopenIssue(@PathVariable Integer id) {

        logger.info("Reopening issue with ID: {}", id);

        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(false);

            Issues reopenedIssue = issuesRepository.save(existing);

            logger.info("Issue reopened successfully with ID: {}", id);

            return ResponseEntity.ok(reopenedIssue);
        }).orElseGet(() -> {
            logger.warn("Cannot reopen. Issue not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Delete issue
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Integer id) {

        logger.info("Deleting issue with ID: {}", id);

        if (issuesRepository.existsById(id)) {
            issuesRepository.deleteById(id);

            logger.info("Issue deleted successfully with ID: {}", id);

            return ResponseEntity.noContent().build();
        }

        logger.warn("Cannot delete. Issue not found with ID: {}", id);

        return ResponseEntity.notFound().build();
    }
}
