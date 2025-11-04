package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks(
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        
    List<TaskDTO> tasks = taskService.getAllTasks(assigneeId, projectId, status, priority);
    ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks, "Retrieved all tasks");
    return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/tasks/{id} - Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task);
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/tasks - Create new task
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        TaskDTO createdTask = taskService.createTask(createDTO);
        ApiResponse<TaskDTO> response = ApiResponse.success(createdTask, "Task created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * PUT /api/tasks/{id} - Full update of task
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskFull(
            @PathVariable Long id,
            @Valid @RequestBody TaskCreateDTO updateDTO) {
        
    TaskDTO updatedTask = taskService.updateTaskFull(id, updateDTO);
    ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task updated successfully");
    return ResponseEntity.ok(response);
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
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable Long id, 
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        
    TaskDTO updatedTask = taskService.updateTask(id, updateDTO);
    ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task partially updated");
    return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/tasks/{id} - Delete task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        ApiResponse<Void> response = ApiResponse.success("Task deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/tasks/{id}/comments - Get comments for a task
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getTaskComments(@PathVariable Long id) {
        List<CommentDTO> comments = taskService.getTaskComments(id);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments, "Retrieved task comments");
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/tasks/{id}/comments - Add comment to task
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDTO>> addCommentToTask(
            @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        
    CommentDTO comment = taskService.addCommentToTask(id, commentCreateDTO);
    ApiResponse<CommentDTO> response = ApiResponse.success(comment, "Comment added to task");
    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * GET /api/tasks/assigned/{userId} - Get tasks assigned to specific user
     */
    @GetMapping("/assigned/{userId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksAssignedToUser(@PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getTasksAssignedToUser(userId);
        ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks, "Retrieved tasks assigned to user");
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/tasks/{id}/assign - Assign task to user
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<TaskDTO>> assignTask(
            @PathVariable Long id,
            @RequestParam Long assigneeId) {
        
    TaskDTO task = taskService.assignTask(id, assigneeId);
    ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task assigned successfully");
    return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/tasks/{id}/unassign - Unassign task
     */
    @PatchMapping("/{id}/unassign")
    public ResponseEntity<ApiResponse<TaskDTO>> unassignTask(@PathVariable Long id) {
        TaskDTO task = taskService.unassignTask(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task unassigned successfully");
        return ResponseEntity.ok(response);
    }
}
