package com.oak.controller;

import com.oak.entity.Comment;
import com.oak.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    // Logger declaration
    private static final Logger logger =
            LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentRepository commentRepository;

    // Get all comments
    @GetMapping
    public List<Comment> getAllComments() {

        logger.info("Fetching all comments");

        List<Comment> comments = commentRepository.findAll();

        logger.info("Successfully fetched {} comments", comments.size());

        return comments;
    }

    // Get comment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {

        logger.info("Fetching comment with ID: {}", id);

        return commentRepository.findById(id)
                .map(comment -> {
                    logger.info("Comment found with ID: {}", id);
                    return ResponseEntity.ok(comment);
                })
                .orElseGet(() -> {
                    logger.warn("Comment not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Get comments by issue ID
    @GetMapping("/issue/{issueId}")
    public List<Comment> getCommentsByIssue(@PathVariable Integer issueId) {

        logger.info("Fetching comments for issue ID: {}", issueId);

        List<Comment> comments = commentRepository.findByIssueId(issueId);

        logger.info("Found {} comments for issue ID: {}",
                comments.size(), issueId);

        return comments;
    }

    // Create comment
    @PostMapping
    public Comment createComment(@RequestBody Comment comment) {

        logger.info("Creating a new comment");

        Comment savedComment = commentRepository.save(comment);

        logger.info("Comment created successfully");

        return savedComment;
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody Comment comment) {

        logger.info("Updating comment with ID: {}", id);

        return commentRepository.findById(id).map(existing -> {
            existing.setContent(comment.getContent());
            existing.setType(comment.getType());

            Comment updatedComment = commentRepository.save(existing);

            logger.info("Comment updated successfully with ID: {}", id);

            return ResponseEntity.ok(updatedComment);
        }).orElseGet(() -> {
            logger.warn("Cannot update. Comment not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        });
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {

        logger.info("Deleting comment with ID: {}", id);

        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);

            logger.info("Comment deleted successfully with ID: {}", id);

            return ResponseEntity.noContent().build();
        }

        logger.warn("Cannot delete. Comment not found with ID: {}", id);

        return ResponseEntity.notFound().build();
    }
}
