package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Demonstrates proper @Transactional usage at both class and method levels
 * 
 * Key Concepts:
 * 1. No class-level @Transactional - allows method-level control
 * 2. Method-level @Transactional with readOnly=true for read operations  
 * 3. Proper transaction boundaries for business logic
 * 4. Write operations (create, update, delete) don't need explicit @Transactional
 *    but benefit from method-level control
 */
@Service
public class CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * CREATE operation - Uses @Transactional for complex business logic
     * This method coordinates 3 separate repository operations:
     * 1. User validation (read)
     * 2. Task validation (read) 
     * 3. Comment creation (write)
     * 
     * Without @Transactional, if Comment.save() fails, User and Task queries
     * would already be committed to database, causing inconsistency.
     */
    @Transactional
    public CommentDTO createComment(CommentCreateDTO commentCreateDTO) {
        // Use mapper to convert DTO to entity
        Comment comment = commentMapper.toEntity(commentCreateDTO);
        
        // Validate relationships - these are part of the same transaction
        User user = userRepository.findById(commentCreateDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Task task = taskRepository.findById(commentCreateDTO.getTaskId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        comment.setUser(user);
        comment.setTask(task);
        
        // Save and convert back to DTO
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * READ operation - Method-level @Transactional with readOnly optimization
     * readOnly=true tells Spring:
     * - Don't flush changes to database
     * - Skip some validation checks
     * - Improves performance for read operations
     */
    @Cacheable(value = "comments", key = "'task_' + #taskId")
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * READ operation - Optimized for read-only access
     */
    @Cacheable(value = "comments", key = "'user_' + #userId")
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * READ operation - Single entity read
     * Still uses @Transactional(readOnly = true) for consistency and optimization
     */
   
    @Cacheable(value = "comments", key = "#id")                 
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentMapper.toDTO(comment);
    }
    
    /**
     * UPDATE operation - Uses @Transactional for consistency
     * Even simple updates benefit from transaction management
     */
     @CachePut(value = "comments", key = "#commentId")
    @Transactional
    public CommentDTO updateComment(Long commentId, String newContent) {
        Comment existingComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        existingComment.setContent(newContent);
        Comment savedComment = commentRepository.save(existingComment);
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * DELETE operation - Uses @Transactional for proper cleanup
     * Ensures delete operation is atomic and properly managed
     */
      @CacheEvict(value = "comments", key = "#commentId")
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
    
    /**
     * Complex business operation - demonstrates transaction boundaries
     * This method shows what happens when you need different transaction behavior
     * 
     * Key points:
     * 1. This method has its own transaction scope
     * 2. If it calls other transactional methods, it can use propagation settings
     * 3. Read-only methods can optimize database access
     */
    @Cacheable(value = "comments", key = "'task_user_info_' + #taskId")
    @Transactional(readOnly = true)  // This method is read-only
    public List<CommentDTO> getTaskCommentsWithUserInfo(Long taskId) {
        // This could join with user information - still read-only
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return commentMapper.toDTOList(comments);
    }
}
