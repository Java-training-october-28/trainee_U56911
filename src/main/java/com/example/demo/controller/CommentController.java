package com.example.demo.controller;

import com.example.demo.dto.*;
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
    public ResponseEntity<CommentDTO> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        CommentDTO createdComment = commentService.createComment(commentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }
    
    /**
     * GET /api/comments/{id} - Get comment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }
    
    /**
     * GET /api/comments/task/{taskId} - Get all comments for a task
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTaskId(@PathVariable Long taskId) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * GET /api/comments/user/{userId} - Get all comments by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUserId(@PathVariable Long userId) {
        List<CommentDTO> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * PATCH /api/comments/{id} - Update comment content
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long id, 
            @RequestBody String newContent) {
        
        CommentDTO updatedComment = commentService.updateComment(id, newContent);
        return ResponseEntity.ok(updatedComment);
    }
    
    /**
     * DELETE /api/comments/{id} - Delete comment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
