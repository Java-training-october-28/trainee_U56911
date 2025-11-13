package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
@Profile({"dev", "test"})
public class DataExportImport {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    public void exportData(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("-- Users --\n");
            for (User user : userRepository.findAll()) {
                writer.write(user.toString() + "\n");
            }
            writer.write("-- Projects --\n");
            for (Project project : projectRepository.findAll()) {
                writer.write(project.toString() + "\n");
            }
            writer.write("-- Tasks --\n");
            for (Task task : taskRepository.findAll()) {
                writer.write(task.toString() + "\n");
            }
        }
    }

    // Import logic would parse a file and save entities
    public void importData(List<User> users, List<Project> projects, List<Task> tasks) {
        userRepository.saveAll(users);
        projectRepository.saveAll(projects);
        taskRepository.saveAll(tasks);
    }
}
