package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.repository.RepositoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repositories")
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
    public Repository getRepositoryById(@PathVariable Long id) {
        return repositoryRepository.findById(id).orElse(null);
    }

    // Create repository
    @PostMapping
    public Repository createRepository(@RequestBody Repository repository) {
        return repositoryRepository.save(repository);
    }

    // Update repository
    @PutMapping("/{id}")
    public Repository updateRepository(
            @PathVariable Long id,
            @RequestBody Repository repository) {

        repository.setId(id);
        return repositoryRepository.save(repository);
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public void deleteRepository(@PathVariable Long id) {
        repositoryRepository.deleteById(id);
    }
}