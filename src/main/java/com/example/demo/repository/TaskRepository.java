package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entity
 * Provides basic CRUD operations and custom query methods
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find tasks by project ID
     */
    List<Task> findByProjectId(Long projectId);
    
    /**
     * Find tasks assigned to a specific user
     */
    List<Task> findByAssigneeId(Long assigneeId);
    
    /**
     * Find tasks by status
     */
    List<Task> findByStatus(String status);
    
    /**
     * Find tasks by priority
     */
    List<Task> findByPriority(String priority);
    
    /**
     * Find overdue tasks (due date has passed)
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find tasks due within a specific period
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find tasks by project and status
     */
    List<Task> findByProjectIdAndStatus(Long projectId, String status);
    
    /**
     * Find tasks by assignee and status
     */
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, String status);
    
    /**
     * Count tasks by project ID
     */
    long countByProjectId(Long projectId);
    
    /**
     * Count tasks by assignee ID
     */
    long countByAssigneeId(Long assigneeId);
}