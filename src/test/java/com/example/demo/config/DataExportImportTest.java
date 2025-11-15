package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.factory.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
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
        User user = TestDataFactory.createUser("imported", "imported@example.com");
        Project project = TestDataFactory.createProject("Imported Project", user);
        Task task = TestDataFactory.createTask("Imported Task", project, user);
        dataExportImport.importData(Collections.singletonList(user), Collections.singletonList(project), Collections.singletonList(task));
        assertTrue(userRepository.findByEmail("imported@example.com").isPresent());
        assertTrue(projectRepository.findById(1L).isPresent());
        assertTrue(taskRepository.findById(1L).isPresent());
    }
}
