package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * POST /api/comments - Create new comment
     * This endpoint demonstrates toEntity() usage
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        CommentDTO createdComment = commentService.createComment(commentCreateDTO);
        ApiResponse<CommentDTO> response = ApiResponse.success(createdComment, "Comment created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * GET /api/comments - Get all comments
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getAllComments() {
        List<CommentDTO> comments = commentService.getAllComments();
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments, "Retrieved all comments");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/comments/{id} - Get comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);
        ApiResponse<CommentDTO> response = ApiResponse.success(comment);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/comments/task/{taskId} - Get all comments for a task
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByTaskId(@PathVariable Long taskId) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments, "Retrieved comments for task");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/comments/user/{userId} - Get all comments by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getCommentsByUserId(@PathVariable Long userId) {
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments, "Retrieved comments by user");
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/comments/{id} - Update comment content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @PathVariable Long id, 
            @RequestBody String newContent) {
        CommentDTO updatedComment = commentService.updateComment(id, newContent);
        ApiResponse<CommentDTO> response = ApiResponse.success(updatedComment, "Comment updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/comments/{id} - Delete comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        ApiResponse<Void> response = ApiResponse.success("Comment deleted successfully");
        return ResponseEntity.ok(response);
    }
}
