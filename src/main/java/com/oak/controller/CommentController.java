package com.oak.controller;

import com.oak.entity.Comment;
import com.oak.repository.CommentRepository;
import com.oak.response.CommentResponse;
import com.oak.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public ApiResponse<List<CommentResponse>> getAllComments() {
        return ApiResponse.success("Comments retrieved successfully",
                commentRepository.findAll().stream().map(CommentResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(CommentResponse::from)
                .map(response -> ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", response)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/issue/{issueId}")
    public ApiResponse<List<CommentResponse>> getCommentsByIssue(@PathVariable Integer issueId) {
        return ApiResponse.success("Comments retrieved successfully",
                commentRepository.findByIssueId(issueId).stream().map(CommentResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody Comment comment) {
        return ApiResponse.success("Comment created successfully",
                CommentResponse.from(commentRepository.save(comment)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return commentRepository.findById(id).map(existing -> {
            existing.setContent(comment.getContent());
            existing.setType(comment.getType());
            return ResponseEntity.ok(ApiResponse.success("Comment updated successfully",
                    CommentResponse.from(commentRepository.save(existing))));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
        }
        return ResponseEntity.notFound().build();
    }
}