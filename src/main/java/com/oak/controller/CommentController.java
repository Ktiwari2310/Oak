package com.oak.controller;

import com.oak.entity.Comment;
import com.oak.repository.CommentRepository;
import com.oak.repository.IssuesRepository;
import com.oak.repository.UserRepository;
import com.oak.response.CommentResponse;
import com.oak.response.ApiResponse;
import com.oak.request.CreateCommentRequest;
import com.oak.request.UpdateCommentRequest;
import com.oak.exception.ResourceNotFoundException;
import com.oak.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    private final CommentRepository commentRepository;
    private final IssuesRepository issuesRepository;
    private final UserRepository userRepository;

    public CommentController(
            CommentRepository commentRepository,
            IssuesRepository issuesRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.issuesRepository = issuesRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    // TODO(security): hide comments on issues in private repositories from non-owners.
    public ApiResponse<Page<CommentResponse>> getAllComments(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<Comment> comments = commentRepository.findAll(pageable);
        logger.info("Comments retrieved successfully: count={}", comments.getNumberOfElements());
        return ApiResponse.success("Comments retrieved successfully",
                comments.map(CommentResponse::from));
    }

    @GetMapping("/{id}")
    // TODO(security): verify access to the comment's issue repository before returning it.
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
        logger.info("Comment retrieved successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", CommentResponse.from(comment)));
    }

    @GetMapping("/issue/{issueId}")
    // TODO(security): verify access to the issue's repository before returning comments.
    public ApiResponse<List<CommentResponse>> getCommentsByIssue(@PathVariable Integer issueId) {
        List<Comment> comments = commentRepository.findByIssueId(issueId);
        logger.info("Comments retrieved successfully for issue: issueId={}, count={}", issueId, comments.size());
        return ApiResponse.success("Comments retrieved successfully",
                comments.stream().map(CommentResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(@Valid @RequestBody CreateCommentRequest request) {
        var issue = issuesRepository.findById(request.issueId()).orElse(null);
        var poster = userRepository.findById(request.posterId()).orElse(null);
        if (issue == null || poster == null) {
            throw new BadRequestException("Issue or poster was not found");
        }

        Comment comment = new Comment();
        comment.setType(request.type());
        comment.setContent(request.content());
        comment.setId(null);
        comment.setIssue(issue);
        comment.setPoster(poster);
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully: id={}", savedComment.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", CommentResponse.from(savedComment)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest comment) {
        Comment existing = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));
        if (comment.content() != null) {
            existing.setContent(comment.content());
        }
        if (comment.type() != null) {
            existing.setType(comment.type());
        }
        Comment updatedComment = commentRepository.save(existing);
        logger.info("Comment updated successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully",
                CommentResponse.from(updatedComment)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found: " + id);
        }
        commentRepository.deleteById(id);
        logger.info("Comment deleted successfully: id={}", id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}