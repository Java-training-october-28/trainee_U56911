package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Pagination support
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Comment> findByTaskId(Long taskId);
    
    void deleteByTaskId(Long taskId);
}
