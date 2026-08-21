package com.oak.controller;

import com.oak.entity.IssuesLabels;
import com.oak.repository.IssuesLabelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issue-labels")
public class IssuesLabelsController {

    @Autowired
    private IssuesLabelsRepository issuesLabelsRepository;

    @GetMapping
    public List<IssuesLabels> getAllIssuesLabels() {
        return issuesLabelsRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssuesLabels> getIssuesLabelById(@PathVariable Integer id) {
        return issuesLabelsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public List<IssuesLabels> getLabelsByIssue(@PathVariable Integer issueId) {
        return issuesLabelsRepository.findByIssueId(issueId);
    }

    @GetMapping("/label/{labelId}")
    public List<IssuesLabels> getIssuesByLabel(@PathVariable Integer labelId) {
        return issuesLabelsRepository.findByLabelId(labelId);
    }

    @PostMapping
    public IssuesLabels createIssuesLabel(@RequestBody IssuesLabels issuesLabel) {
        return issuesLabelsRepository.save(issuesLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssuesLabel(@PathVariable Integer id) {
        if (issuesLabelsRepository.existsById(id)) {
            issuesLabelsRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}