package com.oak.controller;

import com.oak.entity.Comment;
import com.oak.repository.CommentRepository;
import com.oak.response.CommentResponse;
import com.oak.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public ApiResponse<List<CommentResponse>> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        logger.info("Comments retrieved successfully: count={}", comments.size());
        return ApiResponse.success("Comments retrieved successfully",
                comments.stream().map(CommentResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(CommentResponse::from)
                .map(response -> {
                    logger.info("Comment retrieved successfully: id={}", id);
                    return ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", response));
                })
                .orElseGet(() -> {
                    logger.warn("Comment retrieval failed: id={} was not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/issue/{issueId}")
    public ApiResponse<List<CommentResponse>> getCommentsByIssue(@PathVariable Integer issueId) {
        List<Comment> comments = commentRepository.findByIssueId(issueId);
        logger.info("Comments retrieved successfully for issue: issueId={}, count={}", issueId, comments.size());
        return ApiResponse.success("Comments retrieved successfully",
                comments.stream().map(CommentResponse::from).toList());
    }

    @PostMapping
    public ApiResponse<CommentResponse> createComment(@RequestBody Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully: id={}", savedComment.getId());
        return ApiResponse.success("Comment created successfully",
                CommentResponse.from(savedComment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return commentRepository.findById(id).map(existing -> {
            existing.setContent(comment.getContent());
            existing.setType(comment.getType());
            Comment updatedComment = commentRepository.save(existing);
            logger.info("Comment updated successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Comment updated successfully",
                    CommentResponse.from(updatedComment)));
        }).orElseGet(() -> {
            logger.warn("Comment update failed: id={} was not found", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            logger.info("Comment deleted successfully: id={}", id);
            return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
        }
        logger.warn("Comment deletion failed: id={} was not found", id);
        return ResponseEntity.notFound().build();
    }
}