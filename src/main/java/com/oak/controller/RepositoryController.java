package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.entity.User;
import com.oak.repository.RepositoryRepository;
import com.oak.repository.UserRepository;
import com.oak.response.ApiResponse;
import com.oak.response.RepositoryResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all repositories
    @GetMapping
    public ApiResponse<List<RepositoryResponse>> getAllRepositories() {
        List<Repository> repositories = repositoryRepository.findAll();
        logger.info("Repositories retrieved successfully: count={}", repositories.size());

        return ApiResponse.success(
                "Repositories retrieved successfully",
                repositories.stream().map(RepositoryResponse::from).toList());
    }

    // Get repository by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> getRepositoryById(@PathVariable Long id) {

        return repositoryRepository.findById(id)
                .map(RepositoryResponse::from)
                .map(response -> {
                    logger.info("Repository retrieved successfully: id={}", id);
                    return ResponseEntity.ok(
                            ApiResponse.success("Repository retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Repository retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Create repository
    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> createRepository(
            @RequestBody Repository repository) {

if (repository.getOwner() == null || repository.getOwner().getId() == null) {
    return ResponseEntity.badRequest().build();
}
        User owner = userRepository.findById(repository.getOwner().getId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

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
            @RequestBody Repository repository) {

        return repositoryRepository.findById(id).map(existing -> {

            if (repository.getOwner() != null && repository.getOwner().getId() != null) {
                User owner = userRepository.findById(repository.getOwner().getId())
                        .orElseThrow(() -> new RuntimeException("Owner not found"));

                existing.setOwner(owner);
            }

            existing.setName(repository.getName());
            existing.setDescription(repository.getDescription());
            existing.setIs_private(repository.getIs_private());
            existing.setIs_mirror(repository.getIs_mirror());
            existing.setClone_url(repository.getClone_url());

            Repository updatedRepository = repositoryRepository.save(existing);

            logger.info("Repository updated successfully: id={}", id);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            "Repository updated successfully",
                            RepositoryResponse.from(updatedRepository)));

        }).orElseGet(() -> {
            logger.warn("Repository update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRepository(@PathVariable Long id) {

        if (!repositoryRepository.existsById(id)) {
            logger.warn("Repository deletion failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        }

        repositoryRepository.deleteById(id);

        logger.info("Repository deleted successfully: id={}", id);

        return ResponseEntity.ok(
                ApiResponse.success("Repository deleted successfully", null));
    }
}
