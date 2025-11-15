package com.example.demo.repository;

import com.example.demo.entity.Task;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity
 * Provides basic CRUD operations and custom query methods with advanced JPA optimizations
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Basic pagination support
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
    Page<Task> findByStatusAndPriority(TaskStatus status, Priority priority, Pageable pageable);
    
    // Entity graph queries for optimized fetching
    @EntityGraph(value = "Task.withProject", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Task> findWithProjectById(Long id);
    
    @EntityGraph(value = "Task.withAssignee", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Task> findWithAssigneeById(Long id);
    
    @EntityGraph(value = "Task.withProjectAndAssignee", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Task> findWithProjectAndAssigneeById(Long id);
    
    @EntityGraph(value = "Task.withComments", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Task> findWithCommentsById(Long id);
    
    @EntityGraph(value = "Task.withAllRelations", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Task> findWithAllRelationsById(Long id);
    
    // Entity graph queries with pagination
    @EntityGraph(value = "Task.withProject", type = EntityGraph.EntityGraphType.LOAD)
    Page<Task> findWithProjectByProjectId(Long projectId, Pageable pageable);
    
    @EntityGraph(value = "Task.withAssignee", type = EntityGraph.EntityGraphType.LOAD)
    Page<Task> findWithAssigneeByAssigneeId(Long assigneeId, Pageable pageable);
    
    // Basic queries
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByStatus(String status);
    List<Task> findByPriority(String priority);
    
    // Custom JPQL queries
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findTasksDueBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    List<Task> findByProjectIdAndStatus(Long projectId, String status);
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, String status);
    
    // Performance optimized queries
    @Query("SELECT t.id, t.title, t.status, t.priority FROM Task t WHERE t.project.id = :projectId")
    List<Object[]> findTaskSummariesByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT t.id, t.title, t.status, t.priority, COUNT(c) as commentCount " +
           "FROM Task t LEFT JOIN t.comments c WHERE t.project.id = :projectId GROUP BY t.id, t.title, t.status, t.priority")
    List<Object[]> findTaskStatsByProjectId(@Param("projectId") Long projectId);
    
    // Advanced pagination with sorting and filtering
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status ORDER BY t.priority DESC, t.dueDate ASC")
    Page<Task> findTasksByProjectAndStatusWithPriority(@Param("projectId") Long projectId, 
                                                      @Param("status") String status, 
                                                      Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId AND t.dueDate < :currentDate ORDER BY t.dueDate ASC")
    Page<Task> findOverdueTasksByAssignee(@Param("assigneeId") Long assigneeId, 
                                         @Param("currentDate") LocalDateTime currentDate, 
                                         Pageable pageable);
    
    // Search functionality with pagination
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> searchTasksInProject(@Param("projectId") Long projectId, 
                                   @Param("searchTerm") String searchTerm, 
                                   Pageable pageable);
    
    // Bulk operations for performance
    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.id IN :taskIds")
    int updateTaskStatusInBulk(@Param("taskIds") List<Long> taskIds, 
                              @Param("newStatus") TaskStatus newStatus);
    
    @Modifying
    @Query("UPDATE Task t SET t.assignee.id = :assigneeId WHERE t.id IN :taskIds")
    int reassignTasksInBulk(@Param("taskIds") List<Long> taskIds, 
                           @Param("assigneeId") Long assigneeId);
    
    @Modifying
    @Query("UPDATE Task t SET t.updatedAt = CURRENT_TIMESTAMP WHERE t.id IN :taskIds")
    int updateTimestampsForTasks(@Param("taskIds") List<Long> taskIds);
    
    // Count queries
    long countByProjectId(Long projectId);
    long countByAssigneeId(Long assigneeId);
    
    // Advanced count queries
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                  @Param("status") String status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :assigneeId AND t.dueDate < :currentDate")
    long countOverdueTasksByAssignee(@Param("assigneeId") Long assigneeId, 
                                    @Param("currentDate") LocalDateTime currentDate);
    
    // Batch processing support
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId ORDER BY t.id")
    List<Task> findTasksByProjectIdForBatchProcessing(@Param("projectId") Long projectId, 
                                                     Pageable pageable);
}
