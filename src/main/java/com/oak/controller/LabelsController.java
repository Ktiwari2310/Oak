package com.oak.controller;

import com.oak.entity.Labels;
import com.oak.repository.LabelsRepository;
import com.oak.repository.RepositoryRepository;
import com.oak.response.LabelsResponse;
import com.oak.response.ApiResponse;
import com.oak.request.CreateLabelRequest;
import com.oak.request.UpdateLabelRequest;
import com.oak.exception.ResourceNotFoundException;
import com.oak.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelsController {

    private static final Logger logger = LoggerFactory.getLogger(LabelsController.class);

    private final LabelsRepository labelsRepository;
    private final RepositoryRepository repositoryRepository;

    public LabelsController(LabelsRepository labelsRepository, RepositoryRepository repositoryRepository) {
        this.labelsRepository = labelsRepository;
        this.repositoryRepository = repositoryRepository;
    }

    @GetMapping
    // TODO(security): hide labels belonging to private repositories from non-owners.
    public ApiResponse<Page<LabelsResponse>> getAllLabels(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Labels> labels = labelsRepository.findAll(pageable);
        logger.info("Labels retrieved successfully: count={}", labels.getNumberOfElements());
        return ApiResponse.success("Labels retrieved successfully",
                labels.map(LabelsResponse::from));
    }

    @GetMapping("/{id}")
    // TODO(security): verify access to the label's repository before returning it.
    public ResponseEntity<ApiResponse<LabelsResponse>> getLabelById(@PathVariable Integer id) {
        Labels label = labelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
        logger.info("Label retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Label retrieved successfully", LabelsResponse.from(label)));
    }

    @GetMapping("/repository/{repoId}")
    // TODO(security): require repository ownership when the repository is private.
    public ApiResponse<List<LabelsResponse>> getLabelsByRepository(@PathVariable Long repoId) {
        List<Labels> labels = labelsRepository.findByRepositoryId(repoId);
        logger.info("Labels retrieved successfully for repository: repositoryId={}, count={}", repoId, labels.size());
        return ApiResponse.success("Labels retrieved successfully",
                labels.stream().map(LabelsResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LabelsResponse>> createLabel(@Valid @RequestBody CreateLabelRequest request) {
        var repository = repositoryRepository.findById(request.repositoryId()).orElse(null);
        if (repository == null) {
            throw new BadRequestException("Repository was not found");
        }

        Labels label = new Labels();
        label.setName(request.name());
        label.setColor(request.color());
        label.setDescription(request.description());
        label.setId(null);
        label.setRepository(repository);
        Labels savedLabel = labelsRepository.save(label);
        logger.info("Label created successfully: id={}", savedLabel.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Label created successfully", LabelsResponse.from(savedLabel)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelsResponse>> updateLabel(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateLabelRequest label) {
        Labels existing = labelsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found: " + id));
        if (label.name() != null) {
            existing.setName(label.name());
        }
        if (label.color() != null) {
            existing.setColor(label.color());
        }
        if (label.description() != null) {
            existing.setDescription(label.description());
        }
        Labels updatedLabel = labelsRepository.save(existing);
        logger.info("Label updated successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Label updated successfully", LabelsResponse.from(updatedLabel)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLabel(@PathVariable Integer id) {
        if (!labelsRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label not found: " + id);
        }
        labelsRepository.deleteById(id);
        logger.info("Label deleted successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Label deleted successfully", null));
    }
}