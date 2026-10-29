package com.oak.controller;

import com.oak.entity.User;
import com.oak.repository.UserRepository;
import com.oak.response.UserResponse;
import com.oak.response.ApiResponse;
import com.oak.request.CreateUserRequest;
import com.oak.request.UpdateUserRequest;
import com.oak.exception.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        logger.info("Users retrieved successfully: count={}", users.getNumberOfElements());

        return ResponseEntity.ok(ApiResponse.success(
                "Users retrieved successfully",
                users.map(UserResponse::from)));
    }


    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        logger.info("User retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", UserResponse.from(user)));
    }


    // Create user
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {

        User user = new User();
        user.setUsername(request.username());
        user.setFull_name(request.fullName());
        user.setEmail(request.email());
        user.setPassword_hash(passwordEncoder.encode(request.password()));
        user.setIs_admin(false);
        user.setId(null);
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
            @Valid @RequestBody UpdateUserRequest userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (userDetails.username() != null) {
            user.setUsername(userDetails.username());
        }
        if (userDetails.fullName() != null) {
            user.setFull_name(userDetails.fullName());
        }
        if (userDetails.email() != null) {
            user.setEmail(userDetails.email());
        }
        if (userDetails.password() != null) {
            user.setPassword_hash(passwordEncoder.encode(userDetails.password()));
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: id={}", id);

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", UserResponse.from(updatedUser)));
    }


    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
        logger.info("User deleted successfully: id={}", id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}