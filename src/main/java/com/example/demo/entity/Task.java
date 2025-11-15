package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Task.withProject",
        attributeNodes = {
            @NamedAttributeNode("project")
        }
    ),
    @NamedEntityGraph(
        name = "Task.withAssignee",
        attributeNodes = {
            @NamedAttributeNode("assignee")
        }
    ),
    @NamedEntityGraph(
        name = "Task.withProjectAndAssignee",
        attributeNodes = {
            @NamedAttributeNode("project"),
            @NamedAttributeNode("assignee")
        }
    ),
    @NamedEntityGraph(
        name = "Task.withComments",
        attributeNodes = {
            @NamedAttributeNode("comments")
        }
    ),
    @NamedEntityGraph(
        name = "Task.withAllRelations",
        attributeNodes = {
            @NamedAttributeNode("project"),
            @NamedAttributeNode("assignee"),
            @NamedAttributeNode("comments")
        }
    )
})
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Task title is required")
    @Size(min = 2, max = 200, message = "Task title must be between 2 and 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    
    // Constructors
    public Task() {}

    // Constructor used in DataLoader (title, description, project, assignee)
    public Task(String title, String description, Project project, User assignee) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.assignee = assignee;
    }

    public Task(String title, String description, Project project) {
        this.title = title;
        this.description = description;
        this.project = project;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public User getAssignee() { return assignee; }
    public void setAssignee(User assignee) { this.assignee = assignee; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
        public String getName() {
            return this.title;
        }
}
