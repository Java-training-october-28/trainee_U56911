package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.example.demo.exception.ResourceNotFoundException;

@Service
@Transactional
public class ProjectService implements ProjectServiceInterface {
    
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
            .orElseThrow(() -> ResourceNotFoundException.project(projectId));
        
        // 2. Use mapper to update simple fields (name, description, startDate, endDate, status)
        // Only non-null fields from updateDTO will be applied due to IGNORE strategy
        projectMapper.updateEntityFromDTO(updateDTO, existingProject);
        
        // 3. Handle owner relationship update separately with business logic validation
        if (updateDTO.getOwnerId() != null) {
            User newOwner = userRepository.findById(updateDTO.getOwnerId())
                .orElseThrow(() -> ResourceNotFoundException.user(updateDTO.getOwnerId()));
            
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
            .orElseThrow(() -> ResourceNotFoundException.user(createDTO.getOwnerId()));
        project.setOwner(owner);
        
        // Save and return DTO
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDTO(savedProject);
    }

    /**
     * Create project with explicit ownerId parameter (interface compatibility).
     */
    @Override
    public ProjectDTO createProject(ProjectCreateDTO createDTO, Long ownerId) {
        if (createDTO == null) throw new IllegalArgumentException("createDTO cannot be null");
        // ensure ownerId is set on DTO
        createDTO.setOwnerId(ownerId);
        return createProject(createDTO);
    }
    
    /**
     * Get project by ID
     */
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.project(id));
        return projectMapper.toDTO(project);
    }
    
    /**
     * Get project with tasks
     */
    @Transactional(readOnly = true)
    public ProjectDTO getProjectWithTasks(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.project(id));
        return projectMapper.toDTOWithTasks(project);
    }
    
    /**
     * Get all projects
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDTOList(projects);
    }
    
    /**
     * Get all projects for a specific owner
     */
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByOwnerId(Long ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return projectMapper.toDTOList(projects);
    }

    @Override
    public List<ProjectDTO> getProjectsByOwner(Long ownerId) {
        return getProjectsByOwnerId(ownerId);
    }
    
    /**
     * Delete project
     */
    public void deleteProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw ResourceNotFoundException.project(projectId);
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
