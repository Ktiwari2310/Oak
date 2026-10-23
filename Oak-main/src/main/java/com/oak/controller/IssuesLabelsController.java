package com.oak.controller;

import com.oak.entity.IssuesLabels;
import com.oak.repository.IssuesLabelsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issue-labels")
public class IssuesLabelsController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(IssuesLabelsController.class);

    @Autowired
    private IssuesLabelsRepository issuesLabelsRepository;

    // Get all issue-label associations
    @GetMapping
    public List<IssuesLabels> getAllIssuesLabels() {

        logger.info("Fetching all issue-label associations");

        List<IssuesLabels> issuesLabels = issuesLabelsRepository.findAll();

        logger.info("Successfully fetched {} issue-label associations",
                issuesLabels.size());

        return issuesLabels;
    }

    // Get issue-label association by ID
    @GetMapping("/{id}")
    public ResponseEntity<IssuesLabels> getIssuesLabelById(
            @PathVariable Integer id) {

        logger.info("Fetching issue-label association with ID: {}", id);

        return issuesLabelsRepository.findById(id)
                .map(issuesLabel -> {
                    logger.info("Issue-label association found with ID: {}", id);
                    return ResponseEntity.ok(issuesLabel);
                })
                .orElseGet(() -> {
                    logger.warn("Issue-label association not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get labels by issue ID
    @GetMapping("/issue/{issueId}")
    public List<IssuesLabels> getLabelsByIssue(
            @PathVariable Integer issueId) {

        logger.info("Fetching labels for issue ID: {}", issueId);

        List<IssuesLabels> result =
                issuesLabelsRepository.findByIssueId(issueId);

        logger.info("Found {} label associations for issue ID: {}",
                result.size(), issueId);

        return result;
    }

    // Get issues by label ID
    @GetMapping("/label/{labelId}")
    public List<IssuesLabels> getIssuesByLabel(
            @PathVariable Integer labelId) {

        logger.info("Fetching issues for label ID: {}", labelId);

        List<IssuesLabels> result =
                issuesLabelsRepository.findByLabelId(labelId);

        logger.info("Found {} issue associations for label ID: {}",
                result.size(), labelId);

        return result;
    }

    // Create issue-label association
    @PostMapping
    public IssuesLabels createIssuesLabel(
            @RequestBody IssuesLabels issuesLabel) {

        logger.info("Creating a new issue-label association");

        IssuesLabels savedIssuesLabel =
                issuesLabelsRepository.save(issuesLabel);

        logger.info("Issue-label association created successfully");

        return savedIssuesLabel;
    }

    // Delete issue-label association
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssuesLabel(
            @PathVariable Integer id) {

        logger.info("Deleting issue-label association with ID: {}", id);

        if (issuesLabelsRepository.existsById(id)) {
            issuesLabelsRepository.deleteById(id);

            logger.info("Issue-label association deleted successfully with ID: {}",
                    id);

            return ResponseEntity.noContent().build();
        }

        logger.warn("Cannot delete. Issue-label association not found with ID: {}",
                id);

        return ResponseEntity.notFound().build();
    }
}
