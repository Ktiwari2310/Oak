package com.oak.controller;

import com.oak.entity.PullRequest;
import com.oak.repository.PullRequestRepository;
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
    public List<PullRequest> getAllPullRequests() {
        return pullRequestRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PullRequest> getPullRequestById(@PathVariable Long id) {
        return pullRequestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<PullRequest> getPullRequestByIssue(@PathVariable Integer issueId) {
        return pullRequestRepository.findByIssuesId(issueId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/head-repo/{repoId}")
    public List<PullRequest> getPullRequestsByHeadRepo(@PathVariable Long repoId) {
        return pullRequestRepository.findByHeadRepoId(repoId);
    }

    @GetMapping("/base-repo/{repoId}")
    public List<PullRequest> getPullRequestsByBaseRepo(@PathVariable Long repoId) {
        return pullRequestRepository.findByBaseRepoId(repoId);
    }

    @PostMapping
    public PullRequest createPullRequest(@RequestBody PullRequest pullRequest) {
        return pullRequestRepository.save(pullRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PullRequest> updatePullRequest(@PathVariable Long id, @RequestBody PullRequest pullRequest) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setHead_branch(pullRequest.getHead_branch());
            existing.setBase_branch(pullRequest.getBase_branch());
            existing.setMerge_branch_sha(pullRequest.getMerge_branch_sha());
            return ResponseEntity.ok(pullRequestRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/merge")
    public ResponseEntity<PullRequest> mergePullRequest(@PathVariable Long id) {
        return pullRequestRepository.findById(id).map(existing -> {
            existing.setIsMerged(true);
            existing.setMerged_at(LocalDateTime.now());
            return ResponseEntity.ok(pullRequestRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePullRequest(@PathVariable Long id) {
        if (pullRequestRepository.existsById(id)) {
            pullRequestRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}