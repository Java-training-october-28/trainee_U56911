package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "Endpoints for task management")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks with optional filters")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Tasks retrieved")
    })
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks(
            @Parameter(description = "Assignee ID") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Project ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "Task status") @RequestParam(required = false) String status,
            @Parameter(description = "Task priority") @RequestParam(required = false) String priority) {
        
    List<TaskDTO> tasks = taskService.getAllTasks(assigneeId, projectId, status, priority);
    ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks, "Retrieved all tasks");
    return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a task by its ID")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task found"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@Parameter(description = "Task ID") @PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @Operation(summary = "Create new task", description = "Creates a new task")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "201", description = "Task created successfully")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        TaskDTO createdTask = taskService.createTask(createDTO);
        ApiResponse<TaskDTO> response = ApiResponse.success(createdTask, "Task created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates all fields of a task")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task updated successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskFull(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskCreateDTO updateDTO) {
        
    TaskDTO updatedTask = taskService.updateTaskFull(id, updateDTO);
    ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task updated successfully");
    return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update task", description = "Updates only provided fields of a task")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task partially updated"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id, 
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        
    TaskDTO updatedTask = taskService.updateTask(id, updateDTO);
    ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task partially updated");
    return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task by its ID")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task deleted successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteTask(@Parameter(description = "Task ID") @PathVariable Long id) {
        taskService.deleteTask(id);
        ApiResponse<Void> response = ApiResponse.success("Task deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/comments")
    @Operation(summary = "Get comments for task", description = "Retrieves all comments for a task")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Comments retrieved")
    })
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getTaskComments(@Parameter(description = "Task ID") @PathVariable Long id) {
        List<CommentDTO> comments = taskService.getTaskComments(id);
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments, "Retrieved task comments");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to task", description = "Adds a comment to a task")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "201", description = "Comment added to task")
    })
    public ResponseEntity<ApiResponse<CommentDTO>> addCommentToTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        
    CommentDTO comment = taskService.addCommentToTask(id, commentCreateDTO);
    ApiResponse<CommentDTO> response = ApiResponse.success(comment, "Comment added to task");
    return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/assigned/{userId}")
    @Operation(summary = "Get tasks assigned to user", description = "Retrieves all tasks assigned to a specific user")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Tasks retrieved")
    })
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksAssignedToUser(@Parameter(description = "User ID") @PathVariable Long userId) {
        List<TaskDTO> tasks = taskService.getTasksAssignedToUser(userId);
        ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks, "Retrieved tasks assigned to user");
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign task to user", description = "Assigns a task to a user")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task assigned successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> assignTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "Assignee ID") @RequestParam Long assigneeId) {
        
    TaskDTO task = taskService.assignTask(id, assigneeId);
    ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task assigned successfully");
    return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/unassign")
    @Operation(summary = "Unassign task", description = "Unassigns a task from a user")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Task unassigned successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponse<TaskDTO>> unassignTask(@Parameter(description = "Task ID") @PathVariable Long id) {
        TaskDTO task = taskService.unassignTask(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task unassigned successfully");
        return ResponseEntity.ok(response);
    }
}
