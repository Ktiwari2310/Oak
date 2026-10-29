package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import com.oak.response.ApiResponse;
import com.oak.response.RepositoryResponse;
import com.oak.request.CreateRepositoryRequest;
import com.oak.request.UpdateRepositoryRequest;
import com.oak.exception.ResourceNotFoundException;

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

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;

    public RepositoryController(RepositoryRepository repositoryRepository, UserRepository userRepository) {
        this.repositoryRepository = repositoryRepository;
        this.userRepository = userRepository;
    }

    // Get all repositories
    // TODO(security): restrict private repository results to their authenticated owner.
    @GetMapping
    public ApiResponse<Page<RepositoryResponse>> getAllRepositories(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Repository> repositories = repositoryRepository.findAll(pageable);
        logger.info("Repositories retrieved successfully: count={}", repositories.getNumberOfElements());

        return ApiResponse.success(
                "Repositories retrieved successfully",
                repositories.map(RepositoryResponse::from));
    }

    // Get repository by ID
    // TODO(security): reject private repository reads by callers other than the owner.
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> getRepositoryById(@PathVariable Long id) {
        Repository repository = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found: " + id));
        logger.info("Repository retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success(
                "Repository retrieved successfully", RepositoryResponse.from(repository)));
    }

    // Create repository
    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> createRepository(
            @Valid @RequestBody CreateRepositoryRequest request) {

User owner = userRepository.findById(request.ownerId())
        .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + request.ownerId()));

        Repository repository = new Repository();
        repository.setName(request.name());
        repository.setDescription(request.description());
        repository.setIs_private(request.isPrivate());
        repository.setIs_mirror(request.isMirror());
        repository.setClone_url(request.cloneUrl());
        repository.setId(null); // Force INSERT
        repository.setOwner(owner);

        Repository savedRepository = repositoryRepository.save(repository);

        logger.info("Repository created successfully: id={}", savedRepository.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Repository created successfully",
                        RepositoryResponse.from(savedRepository)));
    }

    // Update repository
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> updateRepository(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRepositoryRequest repository) {

        Repository existing = repositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repository not found: " + id));

        if (repository.ownerId() != null) {
            User owner = userRepository.findById(repository.ownerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + repository.ownerId()));
            existing.setOwner(owner);
        }
        if (repository.name() != null) {
            existing.setName(repository.name());
        }
        if (repository.description() != null) {
            existing.setDescription(repository.description());
        }
        if (repository.isPrivate() != null) {
            existing.setIs_private(repository.isPrivate());
        }
        if (repository.isMirror() != null) {
            existing.setIs_mirror(repository.isMirror());
        }
        if (repository.cloneUrl() != null) {
            existing.setClone_url(repository.cloneUrl());
        }

        Repository updatedRepository = repositoryRepository.save(existing);
        logger.info("Repository updated successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success(
                "Repository updated successfully", RepositoryResponse.from(updatedRepository)));
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRepository(@PathVariable Long id) {

        if (!repositoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Repository not found: " + id);
        }

        repositoryRepository.deleteById(id);

        logger.info("Repository deleted successfully: id={}", id);

        return ResponseEntity.ok(
                ApiResponse.success("Repository deleted successfully", null));
    }
}
