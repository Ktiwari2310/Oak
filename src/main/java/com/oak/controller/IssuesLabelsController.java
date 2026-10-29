package com.oak.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.oak.entity.IssuesLabels;
import com.oak.repository.IssuesLabelsRepository;
import com.oak.repository.IssuesRepository;
import com.oak.repository.LabelsRepository;
import com.oak.response.IssuesLabelsResponse;
import com.oak.response.ApiResponse;
import com.oak.request.CreateIssueLabelRequest;
import com.oak.exception.ResourceNotFoundException;
import com.oak.exception.BadRequestException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/issue-labels")
public class IssuesLabelsController {

    private static final Logger logger = LoggerFactory.getLogger(IssuesLabelsController.class);

    private final IssuesLabelsRepository issuesLabelsRepository;
    private final IssuesRepository issuesRepository;
    private final LabelsRepository labelsRepository;

    public IssuesLabelsController(
            IssuesLabelsRepository issuesLabelsRepository,
            IssuesRepository issuesRepository,
            LabelsRepository labelsRepository) {
        this.issuesLabelsRepository = issuesLabelsRepository;
        this.issuesRepository = issuesRepository;
        this.labelsRepository = labelsRepository;
    }

    @GetMapping
    // TODO(security): hide issue-label links belonging to private repositories from non-owners.
    public ApiResponse<Page<IssuesLabelsResponse>> getAllIssuesLabels(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<IssuesLabels> issueLabels = issuesLabelsRepository.findAll(pageable);
        logger.info("Issue labels retrieved successfully: count={}", issueLabels.getNumberOfElements());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.map(IssuesLabelsResponse::from));
    }

    @GetMapping("/{id}")
    // TODO(security): verify access to the linked issue's repository before returning it.
    public ResponseEntity<ApiResponse<IssuesLabelsResponse>> getIssuesLabelById(@PathVariable Integer id) {
        IssuesLabels issueLabel = issuesLabelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue label not found: " + id));
        logger.info("Issue label retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success(
                "Issue label retrieved successfully", IssuesLabelsResponse.from(issueLabel)));
    }

    @GetMapping("/issue/{issueId}")
    // TODO(security): verify access to the issue's repository before returning links.
    public ApiResponse<List<IssuesLabelsResponse>> getLabelsByIssue(@PathVariable Integer issueId) {
        List<IssuesLabels> issueLabels = issuesLabelsRepository.findByIssueId(issueId);
        logger.info("Issue labels retrieved successfully for issue: issueId={}, count={}", issueId, issueLabels.size());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.stream().map(IssuesLabelsResponse::from).toList());
    }

    @GetMapping("/label/{labelId}")
    // TODO(security): verify access to the label's repository before returning links.
    public ApiResponse<List<IssuesLabelsResponse>> getIssuesByLabel(@PathVariable Integer labelId) {
        List<IssuesLabels> issueLabels = issuesLabelsRepository.findByLabelId(labelId);
        logger.info("Issue labels retrieved successfully for label: labelId={}, count={}", labelId, issueLabels.size());
        return ApiResponse.success("Issue labels retrieved successfully",
                issueLabels.stream().map(IssuesLabelsResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IssuesLabelsResponse>> createIssuesLabel(
            @Valid @RequestBody CreateIssueLabelRequest request) {
        var issue = issuesRepository.findById(request.issueId()).orElse(null);
        var label = labelsRepository.findById(request.labelId()).orElse(null);
        if (issue == null || label == null) {
            throw new BadRequestException("Issue or label was not found");
        }
        if (!Objects.equals(issue.getRepository().getId(), label.getRepository().getId())) {
            throw new BadRequestException("Label and issue must belong to the same repository");
        }

        IssuesLabels issuesLabel = new IssuesLabels();
        issuesLabel.setId(null);
        issuesLabel.setIssue(issue);
        issuesLabel.setLabel(label);
        IssuesLabels savedIssueLabel = issuesLabelsRepository.save(issuesLabel);
        logger.info("Issue label created successfully: id={}", savedIssueLabel.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Issue label created successfully", IssuesLabelsResponse.from(savedIssueLabel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssuesLabel(@PathVariable Integer id) {
        if (!issuesLabelsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Issue label not found: " + id);
        }
        issuesLabelsRepository.deleteById(id);
        logger.info("Issue label deleted successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Issue label deleted successfully", null));
    }
}