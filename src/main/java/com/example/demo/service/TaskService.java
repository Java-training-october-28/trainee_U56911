package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dto.TaskUpdateDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Update an existing task with partial data from TaskUpdateDTO
     */
    public TaskDTO updateTask(Long taskId, TaskUpdateDTO updateDTO) {
        // 1. Get existing task from database
        Task existingTask = taskRepository.findById(taskId)
            .orElseThrow(() -> ResourceNotFoundException.task(taskId));
        
        // 2. Use mapper to update simple fields (title, description, status, priority, dueDate)
        // Only non-null fields from updateDTO will be applied due to IGNORE strategy
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        
        // 3. Handle relationship updates separately with business logic validation
        if (updateDTO.getProjectId() != null) {
            Project newProject = projectRepository.findById(updateDTO.getProjectId())
                .orElseThrow(() -> ResourceNotFoundException.project(updateDTO.getProjectId()));
            
            // Add business validation here (e.g., user has permission to assign to this project)
            validateProjectAssignment(existingTask, newProject);
            existingTask.setProject(newProject);
        }
        
        if (updateDTO.getAssigneeId() != null) {
            User newAssignee = userRepository.findById(updateDTO.getAssigneeId())
                .orElseThrow(() -> ResourceNotFoundException.user(updateDTO.getAssigneeId()));
            
            // Add business validation here (e.g., user has required role)
            validateUserAssignment(existingTask, newAssignee);
            existingTask.setAssignee(newAssignee);
        }
        
        // 4. Save updated entity
        Task savedTask = taskRepository.save(existingTask);
        
        // 5. Convert back to DTO for response
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Validate business rules for project assignment
     */
    private void validateProjectAssignment(Task task, Project project) {
        // Example business validation
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new BusinessException("Cannot assign task to a project without a name");
        }
    }
    
    /**
     * Validate business rules for user assignment
     */
    private void validateUserAssignment(Task task, User user) {
        // Example business validation
        if (user.getRole() == null) {
            throw new BusinessException("Cannot assign task to user without a role");
        }
    }
    
    /**
     * Example showing what happens with partial updates
     */
    public void demonstratePartialUpdate() {
        // Assume we have an existing task with these values:
        // id: 1, title: "Old Title", description: "Old Description", 
        // status: "TODO", priority: "LOW", dueDate: "2024-01-01"
        
        Task existingTask = taskRepository.findById(1L).orElseThrow();
        
        // Create update DTO with only some fields
        TaskUpdateDTO partialUpdate = new TaskUpdateDTO();
        partialUpdate.setTitle("Updated Title");        // Will update
        partialUpdate.setStatus("IN_PROGRESS");         // Will update
        // description, priority, dueDate are null      // Will be ignored
        
        // Apply the update
        taskMapper.updateEntityFromDTO(partialUpdate, existingTask);
        
        // Result:
        // title: "Updated Title" ✅ (updated)
        // description: "Old Description" ✅ (preserved)
        // status: "IN_PROGRESS" ✅ (updated)
        // priority: "LOW" ✅ (preserved)
        // dueDate: "2024-01-01" ✅ (preserved)
        // id, createdAt, relationships: unchanged ✅
    }
}
