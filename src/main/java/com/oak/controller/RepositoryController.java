package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.repository.RepositoryRepository;

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
    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }

    // Get repository by ID
    @GetMapping("/{id}")
    public ResponseEntity<Repository> getRepositoryById(@PathVariable Long id) {
        return repositoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create repository
    @PostMapping
    public ResponseEntity<Repository> createRepository(@RequestBody Repository repository) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repositoryRepository.save(repository));
    }

    // Update repository
    @PutMapping("/{id}")
    public ResponseEntity<Repository> updateRepository(
            @PathVariable Long id,
            @RequestBody Repository repository) {

        return repositoryRepository.findById(id).map(existing -> {
            existing.setOwner(repository.getOwner());
            existing.setName(repository.getName());
            existing.setDescription(repository.getDescription());
            existing.setIs_private(repository.getIs_private());
            existing.setIs_mirror(repository.getIs_mirror());
            existing.setClone_url(repository.getClone_url());
            return ResponseEntity.ok(repositoryRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepository(@PathVariable Long id) {
        if (!repositoryRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repositoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}