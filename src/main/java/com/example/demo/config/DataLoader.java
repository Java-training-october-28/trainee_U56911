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
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DataLoader(UserRepository userRepository, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        // Sample Users
        User user1 = new User("alice", "alice@example.com", "password");
        User user2 = new User("bob", "bob@example.com", "password");
        userRepository.save(user1);
        userRepository.save(user2);

        // Sample Projects
        Project project1 = new Project("Project Alpha", "First project", user1);
        Project project2 = new Project("Project Beta", "Second project", user2);
        projectRepository.save(project1);
        projectRepository.save(project2);

        // Sample Tasks
        Task task1 = new Task("Design DB", "Design the database schema", project1, user1);
        Task task2 = new Task("Implement API", "Develop REST API", project2, user2);
        taskRepository.save(task1);
        taskRepository.save(task2);
    }
}
