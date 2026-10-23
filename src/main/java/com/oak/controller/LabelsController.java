package com.oak.controller;

import com.oak.entity.Labels;
import com.oak.repository.LabelsRepository;
import com.oak.response.LabelsResponse;
import com.oak.response.ApiResponse;
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
    public ApiResponse<List<LabelsResponse>> getAllLabels() {
        return ApiResponse.success("Labels retrieved successfully",
                labelsRepository.findAll().stream().map(LabelsResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelsResponse>> getLabelById(@PathVariable Integer id) {
        return labelsRepository.findById(id)
                .map(LabelsResponse::from)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Label retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/repository/{repoId}")
    public ApiResponse<List<LabelsResponse>> getLabelsByRepository(@PathVariable Long repoId) {
        return ApiResponse.success("Labels retrieved successfully",
                labelsRepository.findByRepositoryId(repoId).stream().map(LabelsResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<LabelsResponse> createLabel(@RequestBody Labels label) {
        return ApiResponse.success("Label created successfully", LabelsResponse.from(labelsRepository.save(label)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelsResponse>> updateLabel(@PathVariable Integer id, @RequestBody Labels label) {
        return labelsRepository.findById(id).map(existing -> {
            existing.setName(label.getName());
            existing.setColor(label.getColor());
            existing.setDescription(label.getDescription());
            return ResponseEntity.ok(ApiResponse.success("Label updated successfully",
                    LabelsResponse.from(labelsRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Integer id) {
        if (labelsRepository.existsById(id)) {
            labelsRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Label deleted successfully", null));
        }
        return ResponseEntity.notFound().build();
    }
}