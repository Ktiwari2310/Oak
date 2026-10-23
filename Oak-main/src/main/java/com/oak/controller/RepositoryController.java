package com.oak.controller;

import com.oak.entity.Repository;
import com.oak.repository.RepositoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repositories")
public class RepositoryController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(RepositoryController.class);

    @Autowired
    private RepositoryRepository repositoryRepository;

    // Get all repositories
    @GetMapping
    public List<Repository> getAllRepositories() {

        logger.info("Fetching all repositories");

        List<Repository> repositories = repositoryRepository.findAll();

        logger.info("Successfully fetched {} repositories", repositories.size());

        return repositories;
    }

    // Get repository by ID
    @GetMapping("/{id}")
    public Repository getRepositoryById(@PathVariable Long id) {

        logger.info("Fetching repository with ID: {}", id);

        Repository repository = repositoryRepository.findById(id).orElse(null);

        if (repository == null) {
            logger.warn("Repository not found with ID: {}", id);
        } else {
            logger.info("Repository found with ID: {}", id);
        }

        return repository;
    }

    // Create repository
    @PostMapping
    public Repository createRepository(@RequestBody Repository repository) {

        logger.info("Creating a new repository");

        Repository savedRepository = repositoryRepository.save(repository);

        logger.info("Repository created successfully with ID: {}",
                savedRepository.getId());

        return savedRepository;
    }

    // Update repository
    @PutMapping("/{id}")
    public Repository updateRepository(
            @PathVariable Long id,
            @RequestBody Repository repository) {

        logger.info("Updating repository with ID: {}", id);

        repository.setId(id);

        Repository updatedRepository = repositoryRepository.save(repository);

        logger.info("Repository updated successfully with ID: {}", id);

        return updatedRepository;
    }

    // Delete repository
    @DeleteMapping("/{id}")
    public void deleteRepository(@PathVariable Long id) {

        logger.info("Deleting repository with ID: {}", id);

        repositoryRepository.deleteById(id);

        logger.info("Repository deleted successfully with ID: {}", id);
    }
}
