package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Update existing project with partial data - demonstrates updateEntityFromDTO usage
     */
    public ProjectDTO updateProject(Long projectId, ProjectUpdateDTO updateDTO) {
        // 1. Get existing project from database
        Project existingProject = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // 2. Use mapper to update simple fields (name, description, startDate, endDate, status)
        // Only non-null fields from updateDTO will be applied due to IGNORE strategy
        projectMapper.updateEntityFromDTO(updateDTO, existingProject);
        
        // 3. Handle owner relationship update separately with business logic validation
        if (updateDTO.getOwnerId() != null) {
            User newOwner = userRepository.findById(updateDTO.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Add business validation here (e.g., user has permission to own projects)
            // Check if current user has permission to transfer ownership
            existingProject.setOwner(newOwner);
        }
        
        // 4. Save updated entity
        Project savedProject = projectRepository.save(existingProject);
        
        // 5. Convert back to DTO for response
        return projectMapper.toDTO(savedProject);
    }
    
    /**
     * Create new project - demonstrates toEntity usage
     */
    public ProjectDTO createProject(ProjectCreateDTO createDTO) {
        // Convert DTO to entity (only maps simple fields)
        Project project = projectMapper.toEntity(createDTO);
        
        // Set owner relationship using ownerId from DTO
        User owner = userRepository.findById(createDTO.getOwnerId())
            .orElseThrow(() -> new RuntimeException("Owner not found"));
        project.setOwner(owner);
        
        // Save and return DTO
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDTO(savedProject);
    }
    
    /**
     * Get project by ID
     */
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.toDTO(project);
    }
    
    /**
     * Get project with tasks
     */
    public ProjectDTO getProjectWithTasks(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        return projectMapper.toDTOWithTasks(project);
    }
    
    /**
     * Get all projects for a specific owner
     */
    public List<ProjectDTO> getProjectsByOwnerId(Long ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return projectMapper.toDTOList(projects);
    }
    
    /**
     * Delete project
     */
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }
    
    /**
     * Demonstration of what updateEntityFromDTO does with partial updates
     */
    public void demonstratePartialUpdate() {
        // Assume existing project has:
        // name: "Old Project", description: "Old Description", 
        // status: "PLANNING", startDate: "2024-01-01", endDate: "2024-06-01"
        
        Project existingProject = projectRepository.findById(1L).orElseThrow();
        
        // Create update DTO with only some fields
        ProjectUpdateDTO partialUpdate = new ProjectUpdateDTO();
        partialUpdate.setName("Updated Project Name");     // Will update
        partialUpdate.setStatus("ACTIVE");                 // Will update
        // description, startDate, endDate are null        // Will be ignored
        
        // Apply the update using mapper
        projectMapper.updateEntityFromDTO(partialUpdate, existingProject);
        
        // Result:
        // name: "Updated Project Name" ✅ (updated)
        // description: "Old Description" ✅ (preserved)
        // status: "ACTIVE" ✅ (updated)
        // startDate: "2024-01-01" ✅ (preserved)
        // endDate: "2024-06-01" ✅ (preserved)
        // id, createdAt, owner, tasks: unchanged ✅
    }
}
