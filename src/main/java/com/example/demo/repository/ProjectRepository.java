package com.example.demo.repository;

import com.example.demo.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Pagination support
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    
    List<Project> findByOwnerId(Long ownerId);
    
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    List<Project> findByStatus(String status);
}
