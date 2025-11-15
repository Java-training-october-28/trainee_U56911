package com.example.demo.service;
import java.util.List;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CommentCreateDTO;

import com.example.demo.dto.TaskDTO;
import com.example.demo.dto.TaskCreateDTO;
import com.example.demo.dto.TaskUpdateDTO;
import com.example.demo.entity.TaskStatus;
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
public class TaskService implements TaskServiceInterface {
        // --- Missing methods for TaskController ---
        public List<TaskDTO> getAllTasks(Long assigneeId, Long projectId, String status, String priority) {
            // TODO: Implement filtering logic
            return new java.util.ArrayList<>();
        }

        public TaskDTO updateTaskFull(Long id, TaskCreateDTO updateDTO) {
            // TODO: Implement full update logic
            return null;
        }

        public void deleteTask(Long id) {
            // TODO: Implement delete logic
        }

        public List<CommentDTO> getTaskComments(Long id) {
            // TODO: Implement get comments logic
            return new java.util.ArrayList<>();
        }

        public CommentDTO addCommentToTask(Long id, CommentCreateDTO commentCreateDTO) {
            // TODO: Implement add comment logic
            return null;
        }

        public List<TaskDTO> getTasksAssignedToUser(Long userId) {
            // TODO: Implement assigned tasks logic
            return new java.util.ArrayList<>();
        }

        public TaskDTO assignTask(Long id, Long assigneeId) {
            // TODO: Implement assign logic
            return null;
        }

        public TaskDTO unassignTask(Long id) {
            // TODO: Implement unassign logic
            return null;
        }
    
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

    @Override
    public TaskDTO createTask(TaskCreateDTO createDTO) {
        Task task = taskMapper.toEntity(createDTO);

        // set project
        Project project = projectRepository.findById(createDTO.getProjectId())
            .orElseThrow(() -> ResourceNotFoundException.project(createDTO.getProjectId()));
        task.setProject(project);

        // set assignee if present
        if (createDTO.getAssigneeId() != null) {
            User assignee = userRepository.findById(createDTO.getAssigneeId())
                .orElseThrow(() -> ResourceNotFoundException.user(createDTO.getAssigneeId()));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.task(id));
        return taskMapper.toDTO(task);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<TaskDTO> getTasksByProject(Long projectId) {
        java.util.List<Task> tasks = taskRepository.findByProjectId(projectId);
        return taskMapper.toDTOList(tasks);
    }

    @Override
    public TaskDTO updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.task(id));
        task.setStatus(status);
        Task saved = taskRepository.save(task);
        return taskMapper.toDTO(saved);
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
