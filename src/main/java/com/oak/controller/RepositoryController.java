package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.repository.RepositoryRepository;
import com.oak.response.RepositoryResponse;
import com.oak.response.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    @Autowired
    private RepositoryRepository repositoryRepository;

    // Get all repositories
    @GetMapping
    public ApiResponse<List<RepositoryResponse>> getAllRepositories() {
        return ApiResponse.success("Repositories retrieved successfully",
                repositoryRepository.findAll().stream().map(RepositoryResponse::from).toList());
    }

    // Get repository by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> getRepositoryById(@PathVariable Long id) {
        return repositoryRepository.findById(id)
                .map(RepositoryResponse::from)
                .map(response -> ResponseEntity.ok(
                        ApiResponse.success("Repository retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Create repository
    @PostMapping
    public ResponseEntity<ApiResponse<RepositoryResponse>> createRepository(@RequestBody Repository repository) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Repository created successfully",
                        RepositoryResponse.from(repositoryRepository.save(repository))));
    }

    // Update repository
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RepositoryResponse>> updateRepository(
            @PathVariable Long id,
            @RequestBody Repository repository) {

        return repositoryRepository.findById(id).map(existing -> {
            existing.setOwner(repository.getOwner());
            existing.setName(repository.getName());
            existing.setDescription(repository.getDescription());
            existing.setIs_private(repository.getIs_private());
            existing.setIs_mirror(repository.getIs_mirror());
            existing.setClone_url(repository.getClone_url());
            return ResponseEntity.ok(ApiResponse.success("Repository updated successfully",
                    RepositoryResponse.from(repositoryRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRepository(@PathVariable Long id) {
        if (!repositoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repositoryRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Repository deleted successfully", null));
    }
}