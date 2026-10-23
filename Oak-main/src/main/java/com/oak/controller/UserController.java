package com.oak.controller;

import com.oak.entity.User;
import com.oak.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // 1. Declare the logger
    private static final Logger logger =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        logger.info("Fetching all users");

        List<User> users = userRepository.findAll();

        logger.info("Successfully fetched {} users", users.size());

        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {

        logger.info("Fetching user with ID: {}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("User found with ID: {}", id);
            return ResponseEntity.ok(user.get());
        }

        logger.warn("User not found with ID: {}", id);
        return ResponseEntity.notFound().build();
    }

    // Create user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {

        logger.info("Creating a new user");

        User savedUser = userRepository.save(user);

        logger.info("User created successfully with ID: {}", savedUser.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        logger.info("Updating user with ID: {}", id);

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            logger.warn("Cannot update. User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        user.setUsername(userDetails.getUsername());
        user.setFull_name(userDetails.getFull_name());
        user.setEmail(userDetails.getEmail());
        user.setPassword_hash(userDetails.getPassword_hash());
        user.setIs_admin(userDetails.isIs_admin());

        User updatedUser = userRepository.save(user);

        logger.info("User updated successfully with ID: {}", id);

        return ResponseEntity.ok(updatedUser);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        logger.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.warn("Cannot delete. User not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);

        logger.info("User deleted successfully with ID: {}", id);

        return ResponseEntity.noContent().build();
    }
}