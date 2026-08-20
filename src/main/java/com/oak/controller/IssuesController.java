package com.oak.controller;

import com.oak.entity.Issues;
import com.oak.repository.IssuesRepository;
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
    public List<Issues> getAllIssues() {
        return issuesRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issues> getIssueById(@PathVariable Integer id) {
        return issuesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/repository/{repoId}")
    public List<Issues> getIssuesByRepository(@PathVariable Integer repoId) {
        return issuesRepository.findByRepositoryId(repoId);
    }

    @GetMapping("/repository/{repoId}/number/{issueNumber}")
    public ResponseEntity<Issues> getIssueByRepoAndNumber(@PathVariable Integer repoId,
                                                            @PathVariable Integer issueNumber) {
        return issuesRepository.findByRepositoryIdAndIssueNumber(repoId, issueNumber)
                .stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Issues createIssue(@RequestBody Issues issue) {
        return issuesRepository.save(issue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Issues> updateIssue(@PathVariable Integer id, @RequestBody Issues issue) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setTitle(issue.getTitle());
            existing.setContent(issue.getContent());
            existing.setIsClosed(issue.getIsClosed());
            existing.setUser(issue.getUser());
            existing.setRepository(issue.getRepository());
            existing.setIssueNumber(issue.getIssueNumber());
            return ResponseEntity.ok(issuesRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<Issues> closeIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(true);
            return ResponseEntity.ok(issuesRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<Issues> reopenIssue(@PathVariable Integer id) {
        return issuesRepository.findById(id).map(existing -> {
            existing.setIsClosed(false);
            return ResponseEntity.ok(issuesRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Integer id) {
        if (issuesRepository.existsById(id)) {
            issuesRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}