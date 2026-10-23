package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.repository.IssuesRepository;
import com.oak.response.IssuesResponse;
import com.oak.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssuesController {

    @Autowired
    private IssuesRepository issuesRepository;

    @GetMapping
    public ApiResponse<List<IssuesResponse>> getAllIssues() {
        return ApiResponse.success("Issues retrieved successfully",
                issuesRepository.findAll().stream().map(IssuesResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueById(@PathVariable Integer id) {
        return issuesRepository.findById(id)
                .map(IssuesResponse::from)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/repository/{repoId}")
    public ApiResponse<List<IssuesResponse>> getIssuesByRepository(@PathVariable Integer repoId) {
        return ApiResponse.success("Issues retrieved successfully",
                issuesRepository.findByRepositoryId(repoId).stream().map(IssuesResponse::from).toList());
    }

    @GetMapping("/repository/{repoId}/number/{issueNumber}")
    public ResponseEntity<ApiResponse<IssuesResponse>> getIssueByRepoAndNumber(@PathVariable Integer repoId,
                                                            @PathVariable Integer issueNumber) {
        return issuesRepository.findByRepositoryIdAndIssueNumber(repoId, issueNumber)
                .stream().findFirst()
                .map(IssuesResponse::from)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Issue retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ApiResponse<IssuesResponse> createIssue(@RequestBody Issues issue) {
        return ApiResponse.success("Issue created successfully", IssuesResponse.from(issuesRepository.save(issue)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IssuesResponse>> updateIssue(@PathVariable Integer id, @RequestBody Issues issue) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setTitle(issue.getTitle());
            existing.setContent(issue.getContent());
            existing.setIsClosed(issue.getIsClosed());
            existing.setUser(issue.getUser());
            existing.setRepository(issue.getRepository());
            existing.setIssueNumber(issue.getIssueNumber());
            return ResponseEntity.ok(ApiResponse.success("Issue updated successfully",
                    IssuesResponse.from(issuesRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<IssuesResponse>> closeIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(true);
            return ResponseEntity.ok(ApiResponse.success("Issue closed successfully",
                    IssuesResponse.from(issuesRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<ApiResponse<IssuesResponse>> reopenIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(false);
            return ResponseEntity.ok(ApiResponse.success("Issue reopened successfully",
                    IssuesResponse.from(issuesRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIssue(@PathVariable Integer id) {
        if (issuesRepository.existsById(id)) {
            issuesRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Issue deleted successfully", null));
        }
        return ResponseEntity.notFound().build();
    }
}