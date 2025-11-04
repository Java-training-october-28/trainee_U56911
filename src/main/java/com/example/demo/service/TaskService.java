@Service
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
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        // 2. Use mapper to update simple fields (title, description, status, priority, dueDate)
        // Only non-null fields from updateDTO will be applied due to IGNORE strategy
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        
        // 3. Handle relationship updates separately with business logic validation
        if (updateDTO.getProjectId() != null) {
            Project newProject = projectRepository.findById(updateDTO.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
            // Add business validation here (e.g., user has permission to assign to this project)
            existingTask.setProject(newProject);
        }
        
        if (updateDTO.getAssigneeId() != null) {
            User newAssignee = userRepository.findById(updateDTO.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            // Add business validation here (e.g., user has required role)
            existingTask.setAssignee(newAssignee);
        }
        
        // 4. Save updated entity
        Task savedTask = taskRepository.save(existingTask);
        
        // 5. Convert back to DTO for response
        return taskMapper.toDTO(savedTask);
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
