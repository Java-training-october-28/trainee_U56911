package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * Create a new comment - demonstrates toEntity() usage
     */
    public CommentDTO createComment(CommentCreateDTO commentCreateDTO) {
        // 1. Use mapper to convert DTO to entity (only maps 'content' field)
        Comment comment = commentMapper.toEntity(commentCreateDTO);
        
        // 2. Manually set relationships using IDs from DTO
        User user = userRepository.findById(commentCreateDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Task task = taskRepository.findById(commentCreateDTO.getTaskId())
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        comment.setUser(user);
        comment.setTask(task);
        
        // 3. Save entity and convert back to DTO
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * Get all comments for a specific task
     */
    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * Get all comments by a specific user
     */
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * Get comment by ID
     */
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentMapper.toDTO(comment);
    }
    
    /**
     * Update comment content (only content can be updated)
     */
    public CommentDTO updateComment(Long commentId, String newContent) {
        Comment existingComment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        existingComment.setContent(newContent);
        Comment savedComment = commentRepository.save(existingComment);
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * Delete comment
     */
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }
}
