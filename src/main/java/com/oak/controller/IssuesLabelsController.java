package com.oak.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oak.entity.IssuesLabels;
import com.oak.repository.IssuesLabelsRepository;
import com.oak.response.IssuesLabelsResponse;
import com.oak.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/issue-labels")
public class IssuesLabelsController {

    private static final Logger logger = LoggerFactory.getLogger(IssuesLabelsController.class);

    @Autowired
    private IssuesLabelsRepository issuesLabelsRepository;

    @GetMapping
    public ApiResponse<List<IssuesLabelsResponse>> getAllIssuesLabels() {
        List<IssuesLabels> issueLabels = issuesLabelsRepository.findAll();
        logger.info("Issue labels retrieved successfully: count={}", issueLabels.size());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.stream().map(IssuesLabelsResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesLabelsResponse>> getIssuesLabelById(@PathVariable Integer id) {
        return issuesLabelsRepository.findById(id)
                .map(IssuesLabelsResponse::from)
                .map(response -> {
                    logger.info("Issue label retrieved successfully: id={}", id);
                    return ResponseEntity.ok(ApiResponse.success("Issue label retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Issue label retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/issue/{issueId}")
    public ApiResponse<List<IssuesLabelsResponse>> getLabelsByIssue(@PathVariable Integer issueId) {
        List<IssuesLabels> issueLabels = issuesLabelsRepository.findByIssueId(issueId);
        logger.info("Issue labels retrieved successfully for issue: issueId={}, count={}", issueId, issueLabels.size());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.stream().map(IssuesLabelsResponse::from).toList());
    }

    @GetMapping("/label/{labelId}")
    public ApiResponse<List<IssuesLabelsResponse>> getIssuesByLabel(@PathVariable Integer labelId) {
        List<IssuesLabels> issueLabels = issuesLabelsRepository.findByLabelId(labelId);
        logger.info("Issue labels retrieved successfully for label: labelId={}, count={}", labelId, issueLabels.size());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.stream().map(IssuesLabelsResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<IssuesLabelsResponse> createIssuesLabel(@RequestBody IssuesLabels issuesLabel) {
        IssuesLabels savedIssueLabel = issuesLabelsRepository.save(issuesLabel);
        logger.info("Issue label created successfully: id={}", savedIssueLabel.getId());
        return ApiResponse.success("Issue label created successfully", IssuesLabelsResponse.from(savedIssueLabel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssuesLabel(@PathVariable Integer id) {
        if (issuesLabelsRepository.existsById(id)) {
            issuesLabelsRepository.deleteById(id);
            logger.info("Issue label deleted successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Issue label deleted successfully", null));
        }
        logger.warn("Issue label deletion failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }
}