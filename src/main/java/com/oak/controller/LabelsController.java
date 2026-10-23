package com.oak.controller;

import com.oak.entity.Labels;
import com.oak.repository.LabelsRepository;
import com.oak.response.LabelsResponse;
import com.oak.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {

    private static final Logger logger = LoggerFactory.getLogger(LabelsController.class);

    @Autowired
    private LabelsRepository labelsRepository;

    @GetMapping
    public ApiResponse<List<LabelsResponse>> getAllLabels() {
        List<Labels> labels = labelsRepository.findAll();
        logger.info("Labels retrieved successfully: count={}", labels.size());
        return ApiResponse.success("Labels retrieved successfully",
                labels.stream().map(LabelsResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelsResponse>> getLabelById(@PathVariable Integer id) {
        return labelsRepository.findById(id)
                .map(LabelsResponse::from)
                .map(response -> {
                    logger.info("Label retrieved successfully: id={}", id);
                    return ResponseEntity.ok(ApiResponse.success("Label retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Label retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/repository/{repoId}")
    public ApiResponse<List<LabelsResponse>> getLabelsByRepository(@PathVariable Long repoId) {
        List<Labels> labels = labelsRepository.findByRepositoryId(repoId);
        logger.info("Labels retrieved successfully for repository: repositoryId={}, count={}", repoId, labels.size());
        return ApiResponse.success("Labels retrieved successfully",
                labels.stream().map(LabelsResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<LabelsResponse> createLabel(@RequestBody Labels label) {
        Labels savedLabel = labelsRepository.save(label);
        logger.info("Label created successfully: id={}", savedLabel.getId());
        return ApiResponse.success("Label created successfully", LabelsResponse.from(savedLabel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelsResponse>> updateLabel(@PathVariable Integer id, @RequestBody Labels label) {
        return labelsRepository.findById(id).map(existing -> {
            existing.setName(label.getName());
            existing.setColor(label.getColor());
            existing.setDescription(label.getDescription());
            Labels updatedLabel = labelsRepository.save(existing);
            logger.info("Label updated successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Label updated successfully", LabelsResponse.from(updatedLabel)));
        }).orElseGet(() -> {
            logger.warn("Label update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Integer id) {
        if (labelsRepository.existsById(id)) {
            labelsRepository.deleteById(id);
            logger.info("Label deleted successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Label deleted successfully", null));
        }
        logger.warn("Label deletion failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }
}