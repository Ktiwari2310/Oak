package com.oak.controller;

import com.oak.entity.User;
import com.oak.repository.UserRepository;
import com.oak.response.UserResponse;
import com.oak.response.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<User> users = userRepository.findAll();
        logger.info("Users retrieved successfully: count={}", users.size());

        return ResponseEntity.ok(ApiResponse.success(
                "Users retrieved successfully",
                users.stream().map(UserResponse::from).toList()));
    }


    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            logger.info("User retrieved successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", UserResponse.from(user.get())));
        }

        logger.warn("User retrieval failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }


    // Create user
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody User user) {

        // Hash password before saving to database
        if (user.getPassword_hash() != null && !user.getPassword_hash().isBlank()) {
            user.setPassword_hash(passwordEncoder.encode(user.getPassword_hash()));
        }

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: id={}", savedUser.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", UserResponse.from(savedUser)));
    }


    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            logger.warn("User update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        user.setUsername(userDetails.getUsername());
        user.setFull_name(userDetails.getFull_name());
        user.setEmail(userDetails.getEmail());

        // Only re-hash and update if a new password was provided
        if (userDetails.getPassword_hash() != null && !userDetails.getPassword_hash().isBlank()) {
            user.setPassword_hash(passwordEncoder.encode(userDetails.getPassword_hash()));
        }

        user.setIs_admin(userDetails.isIs_admin());

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: id={}", id);

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", UserResponse.from(updatedUser)));
    }


    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            logger.warn("User deletion failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully: id={}", id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}