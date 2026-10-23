package com.oak.controller;

import com.oak.entity.Labels;
import com.oak.repository.LabelsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(LabelsController.class);

    @Autowired
    private LabelsRepository labelsRepository;

    // Get all labels
    @GetMapping
    public List<Labels> getAllLabels() {

        logger.info("Fetching all labels");

        List<Labels> labels = labelsRepository.findAll();

        logger.info("Successfully fetched {} labels", labels.size());

        return labels;
    }

    // Get label by ID
    @GetMapping("/{id}")
    public ResponseEntity<Labels> getLabelById(@PathVariable Integer id) {

        logger.info("Fetching label with ID: {}", id);

        return labelsRepository.findById(id)
                .map(label -> {
                    logger.info("Label found with ID: {}", id);
                    return ResponseEntity.ok(label);
                })
                .orElseGet(() -> {
                    logger.warn("Label not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get labels by repository ID
    @GetMapping("/repository/{repoId}")
    public List<Labels> getLabelsByRepository(@PathVariable Integer repoId) {

        logger.info("Fetching labels for repository ID: {}", repoId);

        List<Labels> labels = labelsRepository.findByRepositoryId(repoId);

        logger.info("Found {} labels for repository ID: {}",
                labels.size(), repoId);

        return labels;
    }

    // Create label
    @PostMapping
    public Labels createLabel(@RequestBody Labels label) {

        logger.info("Creating a new label");

        Labels savedLabel = labelsRepository.save(label);

        logger.info("Label created successfully with ID: {}",
                savedLabel.getId());

        return savedLabel;
    }

    // Update label
    @PutMapping("/{id}")
    public ResponseEntity<Labels> updateLabel(
            @PathVariable Integer id,
            @RequestBody Labels label) {

        logger.info("Updating label with ID: {}", id);

        return labelsRepository.findById(id).map(existing -> {
            existing.setName(label.getName());
            existing.setColor(label.getColor());
            existing.setDescription(label.getDescription());

            Labels updatedLabel = labelsRepository.save(existing);

            logger.info("Label updated successfully with ID: {}", id);

            return ResponseEntity.ok(updatedLabel);
        }).orElseGet(() -> {
            logger.warn("Cannot update. Label not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Delete label
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Integer id) {

        logger.info("Deleting label with ID: {}", id);

        if (labelsRepository.existsById(id)) {
            labelsRepository.deleteById(id);

            logger.info("Label deleted successfully with ID: {}", id);

            return ResponseEntity.noContent().build();
        }

        logger.warn("Cannot delete. Label not found with ID: {}", id);

        return ResponseEntity.notFound().build();
    }
}
