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

@RestController
@RequestMapping("/api/issue-labels")
public class IssuesLabelsController {

    @Autowired
    private IssuesLabelsRepository issuesLabelsRepository;

    @GetMapping
    public ApiResponse<List<IssuesLabelsResponse>> getAllIssuesLabels() {
        return ApiResponse.success("Issue labels retrieved successfully",
                issuesLabelsRepository.findAll().stream().map(IssuesLabelsResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesLabelsResponse>> getIssuesLabelById(@PathVariable Integer id) {
        return issuesLabelsRepository.findById(id)
                .map(IssuesLabelsResponse::from)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Issue label retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public ApiResponse<List<IssuesLabelsResponse>> getLabelsByIssue(@PathVariable Integer issueId) {
        return ApiResponse.success("Issue labels retrieved successfully",
                issuesLabelsRepository.findByIssueId(issueId).stream().map(IssuesLabelsResponse::from).toList());
    }

    @GetMapping("/label/{labelId}")
    public ApiResponse<List<IssuesLabelsResponse>> getIssuesByLabel(@PathVariable Integer labelId) {
        return ApiResponse.success("Issue labels retrieved successfully",
                issuesLabelsRepository.findByLabelId(labelId).stream().map(IssuesLabelsResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<IssuesLabelsResponse> createIssuesLabel(@RequestBody IssuesLabels issuesLabel) {
        return ApiResponse.success("Issue label created successfully",
                IssuesLabelsResponse.from(issuesLabelsRepository.save(issuesLabel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssuesLabel(@PathVariable Integer id) {
        if (issuesLabelsRepository.existsById(id)) {
            issuesLabelsRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Issue label deleted successfully", null));
        }
        return ResponseEntity.notFound().build();
    }
}