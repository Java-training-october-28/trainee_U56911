package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * GET /api/tasks - Get all tasks (with optional filtering)
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        
        List<TaskDTO> tasks = taskService.getAllTasks(assigneeId, projectId, status, priority);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/{id} - Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    /**
     * POST /api/tasks - Create new task
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        TaskDTO createdTask = taskService.createTask(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    /**
     * PUT /api/tasks/{id} - Full update of task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTaskFull(
            @PathVariable Long id,
            @Valid @RequestBody TaskCreateDTO updateDTO) {
        
        TaskDTO updatedTask = taskService.updateTaskFull(id, updateDTO);
        return ResponseEntity.ok(updatedTask);
    }
    
    /**
     * PATCH endpoint for partial task updates
     * Example usage:
     * PATCH /api/tasks/1
     * {
     *   "title": "New Title",
     *   "status": "COMPLETED"
     * }
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id, 
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        
        TaskDTO updatedTask = taskService.updateTask(id, updateDTO);
        return ResponseEntity.ok(updatedTask);
    }
    
    /**
     * DELETE /api/tasks/{id} - Delete task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/tasks/{id}/comments - Get comments for a task
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComments(@PathVariable Long id) {
        List<CommentDTO> comments = taskService.getTaskComments(id);
        return ResponseEntity.ok(comments);
    }
    
    /**
     * POST /api/tasks/{id}/comments - Add comment to task
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addCommentToTask(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        
        CommentDTO comment = taskService.addCommentToTask(id, commentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
    
    /**
     * GET /api/tasks/assigned/{userId} - Get tasks assigned to specific user
     */
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksAssignedToUser(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getTasksAssignedToUser(userId);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * PATCH /api/tasks/{id}/assign - Assign task to user
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long id,
            @RequestParam Long assigneeId) {
        
        TaskDTO task = taskService.assignTask(id, assigneeId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * PATCH /api/tasks/{id}/unassign - Unassign task
     */
    @PatchMapping("/{id}/unassign")
    public ResponseEntity<TaskDTO> unassignTask(@PathVariable Long id) {
        TaskDTO task = taskService.unassignTask(id);
        return ResponseEntity.ok(task);
    }
}
