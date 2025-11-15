package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Management", description = "Project management endpoints")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * POST /api/projects - Create new project
     */
    @PostMapping
    @Operation(summary = "Create new project", description = "Creates a new project with the provided details")
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        ProjectDTO createdProject = projectService.createProject(createDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(createdProject, "Project created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * GET /api/projects - Get all projects
     */
    @GetMapping
    @Operation(summary = "Get all projects", description = "Retrieves all projects")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        ApiResponse<List<ProjectDTO>> response = ApiResponse.success(projects, "Retrieved all projects");
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/projects/{id} - Get project by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Retrieves a project by its ID")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProject(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        ApiResponse<ProjectDTO> response = ApiResponse.success(project);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/projects/{id}/tasks - Get project with tasks
     */
    @GetMapping("/{id}/tasks")
    @Operation(summary = "Get project with tasks", description = "Retrieves a project with all its tasks")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectWithTasks(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectWithTasks(id);
        ApiResponse<ProjectDTO> response = ApiResponse.success(project);
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET /api/projects/owner/{ownerId} - Get all projects for an owner
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get projects by owner", description = "Retrieves all projects owned by a specific user")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByOwner(@PathVariable Long ownerId) {
        List<ProjectDTO> projects = projectService.getProjectsByOwnerId(ownerId);
        ApiResponse<List<ProjectDTO>> response = ApiResponse.success(projects, 
            String.format("Retrieved %d projects", projects.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT /api/projects/{id} - Full update of project
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Updates all fields of a project")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateDTO updateDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, updateDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(updatedProject, "Project updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * PATCH /api/projects/{id} - Partial update of project
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update project", description = "Updates only the provided fields of a project")
    public ResponseEntity<ApiResponse<ProjectDTO>> partialUpdateProject(
            @PathVariable Long id,
            @RequestBody ProjectUpdateDTO updateDTO) {
        
        ProjectDTO updatedProject = projectService.updateProject(id, updateDTO);
        ApiResponse<ProjectDTO> response = ApiResponse.success(updatedProject, "Project partially updated");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/projects/{id} - Delete project
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Deletes a project by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        ApiResponse<Void> response = ApiResponse.success("Project deleted successfully");
        return ResponseEntity.ok(response);
    }
}
