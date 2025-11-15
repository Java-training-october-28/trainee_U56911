package com.example.demo.repository;

import com.example.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    // Basic queries with pagination
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    
    List<Project> findByOwnerId(Long ownerId);
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    // Entity graph queries for optimized fetching
    @EntityGraph(value = "Project.withOwner", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Project> findWithOwnerById(Long id);
    
    @EntityGraph(value = "Project.withOwnerAndTasks", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Project> findWithOwnerAndTasksById(Long id);
    
    @EntityGraph(value = "Project.withOwner", type = EntityGraph.EntityGraphType.LOAD)
    List<Project> findWithOwnerByOwnerId(Long ownerId);
    
    @EntityGraph(value = "Project.withOwner", type = EntityGraph.EntityGraphType.LOAD)
    Page<Project> findWithOwnerByOwnerId(Long ownerId, Pageable pageable);
    
    // Custom JPQL queries with entity graphs
    @EntityGraph(value = "Project.withOwnerAndTasks", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND p.name LIKE %:name%")
    List<Project> findWithOwnerAndTasksByOwnerIdAndNameContaining(@Param("ownerId") Long ownerId, 
                                                                  @Param("name") String name);
    
    // Performance optimized queries
    @Query("SELECT p.id, p.name, p.description FROM Project p WHERE p.owner.id = :ownerId")
    List<Object[]> findProjectSummariesByOwnerId(@Param("ownerId") Long ownerId);
    
    @Query("SELECT p.id, p.name, COUNT(t) as taskCount FROM Project p LEFT JOIN p.tasks t WHERE p.owner.id = :ownerId GROUP BY p.id, p.name")
    List<Object[]> findProjectStatsByOwnerId(@Param("ownerId") Long ownerId);
    
    // Advanced pagination with sorting
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId ORDER BY p.createdAt DESC")
    Page<Project> findRecentProjectsByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId AND p.name LIKE %:searchTerm%")
    Page<Project> searchProjectsByOwnerId(@Param("ownerId") Long ownerId, 
                                         @Param("searchTerm") String searchTerm, 
                                         Pageable pageable);
    
    // Bulk operations
    @Query("UPDATE Project p SET p.updatedAt = CURRENT_TIMESTAMP WHERE p.id IN :ids")
    int updateTimestampsForProjects(@Param("ids") List<Long> ids);
}
