package com.oak.controller;

import com.oak.entity.Labels;
import com.oak.repository.LabelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {

    @Autowired
    private LabelsRepository labelsRepository;

    @GetMapping
    public List<Labels> getAllLabels() {
        return labelsRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Labels> getLabelById(@PathVariable Integer id) {
        return labelsRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/repository/{repoId}")
    public List<Labels> getLabelsByRepository(@PathVariable Integer repoId) {
        return labelsRepository.findByRepositoryId(repoId);
    }

    @PostMapping
    public Labels createLabel(@RequestBody Labels label) {
        return labelsRepository.save(label);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Labels> updateLabel(@PathVariable Integer id, @RequestBody Labels label) {
        return labelsRepository.findById(id).map(existing -> {
            existing.setName(label.getName());
            existing.setColor(label.getColor());
            existing.setDescription(label.getDescription());
            return ResponseEntity.ok(labelsRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Integer id) {
        if (labelsRepository.existsById(id)) {
            labelsRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}