package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class DataValidation implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DataValidation(UserRepository userRepository, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        // Validate Users
        userRepository.findAll().forEach(user -> {
            if (user.getUsername() == null || user.getEmail() == null) {
                throw new IllegalStateException("User validation failed: " + user);
            }
        });
        // Validate Projects
        projectRepository.findAll().forEach(project -> {
            if (project.getName() == null || project.getOwner() == null) {
                throw new IllegalStateException("Project validation failed: " + project);
            }
        });
        // Validate Tasks
        taskRepository.findAll().forEach(task -> {
            if (task.getName() == null || task.getProject() == null || task.getAssignee() == null) {
                throw new IllegalStateException("Task validation failed: " + task);
            }
        });
    }
}
