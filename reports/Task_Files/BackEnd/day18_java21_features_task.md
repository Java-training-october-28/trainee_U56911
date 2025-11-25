# Day 18: Java 21 Features Integration & Modernization
**Duration**: 8 hours  
**Learning Objectives**:
- Implement Java 21 language features in the Task Management System
- Leverage modern Java capabilities for cleaner, more efficient code
- Apply pattern matching, records, and sealed classes in real-world scenarios
- Optimize performance with virtual threads and structured concurrency

**Exercises**:

## 1. Morning (4 hours): Java 21 Language Features Implementation

### Exercise 1.1: Records for DTOs (1 hour)
- Convert existing DTO classes to Java records where appropriate
- Replace UserDTO, ProjectDTO, TaskDTO with record implementations
- Implement validation in record constructors using compact constructors
- Update MapStruct mappers to work with records

**Implementation Steps**:
```java
// Convert UserDTO to record
public record UserDTO(
    Long id,
    @NotBlank String username,
    @Email String email,
    Role role,
    LocalDateTime createdAt
) {
    public UserDTO {
        // Compact constructor for validation
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
    }
}
```

### Exercise 1.2: Sealed Classes for Entity Hierarchy (1.5 hours)
- Create sealed hierarchy for notification types
- Implement sealed interfaces for service contracts
- Use sealed classes for different task types

**Implementation Steps**:
```java
// Sealed interface for notification types
public sealed interface Notification 
    permits EmailNotification, PushNotification, SystemNotification {
    
    String getMessage();
    LocalDateTime getTimestamp();
}

// Sealed classes for different notification types
public final record EmailNotification(
    String recipient,
    String subject,
    String message,
    LocalDateTime timestamp
) implements Notification {
    // Implementation
}

public final record PushNotification(
    String deviceToken,
    String title,
    String message,
    LocalDateTime timestamp
) implements Notification {
    // Implementation
}
```

### Exercise 1.3: Pattern Matching & Switch Expressions (1.5 hours)
- Replace traditional if-else chains with pattern matching
- Use switch expressions for status handling
- Implement pattern matching in service methods

**Implementation Steps**:
```java
// Pattern matching in TaskService
public String getTaskStatusDescription(Task task) {
    return switch (task.getStatus()) {
        case TaskStatus.PENDING -> "Task is waiting to be started";
        case TaskStatus.IN_PROGRESS -> "Task is currently being worked on";
        case TaskStatus.COMPLETED -> "Task has been completed successfully";
        case TaskStatus.CANCELLED -> "Task has been cancelled";
        default -> "Unknown status";
    };
}

// Pattern matching with instanceof
public void processNotification(Notification notification) {
    if (notification instanceof EmailNotification email) {
        emailService.send(email.recipient(), email.subject(), email.message());
    } else if (notification instanceof PushNotification push) {
        pushService.send(push.deviceToken(), push.title(), push.message());
    }
}
```

## 2. Afternoon (4 hours): Advanced Java 21 Features & Performance

### Exercise 2.1: Virtual Threads for I/O Operations (2 hours)
- Implement virtual threads for database operations
- Create async service methods using virtual threads
- Replace traditional thread pools with virtual threads
- Test performance improvements

**Implementation Steps**:
```java
@Service
public class AsyncTaskService {
    
    @Async
    public CompletableFuture<List<TaskDTO>> getTasksByProjectAsync(Long projectId) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return CompletableFuture.supplyAsync(() -> {
                List<Task> tasks = taskRepository.findByProjectId(projectId);
                return taskMapper.toDtoList(tasks);
            }, executor);
        }
    }
    
    // Batch processing with virtual threads
    public void processTasksInParallel(List<Long> taskIds) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = taskIds.stream()
                .map(taskId -> CompletableFuture.runAsync(() -> 
                    processSingleTask(taskId), executor))
                .toList();
            
            // Wait for all tasks to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }
}
```

### Exercise 2.2: Structured Concurrency for Complex Operations (1 hour)
- Implement structured concurrency for batch operations
- Create scoped values for request context
- Handle task dependencies with structured task scopes

**Implementation Steps**:
```java
@Service
public class BatchProcessingService {
    
    public BatchResult processProjectTasks(Long projectId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Fork subtasks
            Subtask<List<Task>> tasksFuture = scope.fork(() -> 
                taskRepository.findByProjectId(projectId));
            Subtask<Project> projectFuture = scope.fork(() -> 
                projectRepository.findById(projectId).orElseThrow());
            Subtask<List<User>> usersFuture = scope.fork(() -> 
                userRepository.findByProjectId(projectId));
            
            // Join all results
            scope.join();
            scope.throwIfFailed();
            
            return new BatchResult(
                tasksFuture.get(),
                projectFuture.get(),
                usersFuture.get()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch processing interrupted", e);
        }
    }
}
```

### Exercise 2.3: String Templates & Enhanced APIs (1 hour)
- Use string templates for SQL queries and logging
- Implement sequenced collections for ordered operations
- Leverage new collection methods

**Implementation Steps**:
```java
// String templates for dynamic queries
public String buildTaskSearchQuery(String title, TaskStatus status, LocalDate dueDate) {
    return STR."""
        SELECT t FROM Task t 
        WHERE t.title LIKE '%\{title}%' 
        AND t.status = \{status} 
        AND t.dueDate <= '\{dueDate}'
        ORDER BY t.priority DESC, t.dueDate ASC
    """;
}

// Sequenced collections for task priorities
public void reorderTasksByPriority(List<Task> tasks) {
    // Using new collection methods
    tasks.sort(Comparator.comparing(Task::getPriority).reversed());
    
    // Sequenced collection operations
    var sequencedTasks = new LinkedHashSet<>(tasks);
    Task highestPriority = sequencedTasks.getFirst();
    Task lowestPriority = sequencedTasks.getLast();
    
    // Move completed tasks to end
    sequencedTasks.removeIf(task -> task.getStatus() == TaskStatus.COMPLETED);
    sequencedTasks.addAll(tasks.stream()
        .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
        .toList());
}
```

## Deliverables:
- [ ] All DTOs converted to records with proper validation
- [ ] Sealed class hierarchy implemented for notifications
- [ ] Pattern matching and switch expressions applied throughout codebase
- [ ] Virtual threads implemented for async operations
- [ ] Structured concurrency used for batch processing
- [ ] String templates and enhanced APIs utilized
- [ ] Performance improvements measured and documented
- [ ] Comprehensive tests for new Java 21 features

## GitHub Check:
- Java 21 features integrated into existing codebase
- All tests passing with new implementations
- Performance benchmarks showing improvements
- Code quality maintained or improved

## Learning Outcomes:
By completing Day 21, participants will:
- Understand and apply Java 21 language features in real-world scenarios
- Implement modern concurrency patterns with virtual threads
- Use pattern matching and sealed classes for type-safe code
- Leverage records for immutable data transfer
- Apply structured concurrency for complex operations
- Utilize string templates and enhanced collection APIs
- Measure and optimize application performance

## Prerequisites:
- Completion of Days 1-20 training tasks
- Java 21 JDK installed and configured
- Understanding of basic Java concepts and Spring Boot
- Familiarity with concurrent programming concepts

## Assessment Criteria:
- **Code Quality (30%)**: Proper use of Java 21 features, clean implementation
- **Functionality (30%)**: All features working as expected, no regressions
- **Performance (20%)**: Measurable performance improvements
- **Testing (20%)**: Comprehensive tests for new features

**Total Points**: 100  
**Passing Score**: 70 points
