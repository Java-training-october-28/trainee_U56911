package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DataExportImportTest {
    @Autowired
    private DataExportImport dataExportImport;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testExportData() throws IOException {
        String filePath = "exported-data.txt";
        dataExportImport.exportData(filePath);
        File file = new File(filePath);
        assertTrue(file.exists() && file.length() > 0);
        file.delete();
    }

    @Test
    void testImportData() {
        User user = new User("imported", "imported@example.com", "password");
        Project project = new Project("Imported Project", "desc", user);
        Task task = new Task("Imported Task", "details", project, user);
        dataExportImport.importData(Collections.singletonList(user), Collections.singletonList(project), Collections.singletonList(task));
        assertTrue(userRepository.findByUsername("imported").isPresent());
        assertTrue(projectRepository.findByName("Imported Project").isPresent());
        assertTrue(taskRepository.findByName("Imported Task").isPresent());
    }
}
