package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.config.TestSecurityConfig;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@Transactional
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    private User testUser;
    private Project testProject;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        testUser = userRepository.save(TestDataFactory.createUser("testuser", "test@email.com"));
        testProject = projectRepository.save(TestDataFactory.createProject("Test Project", testUser));
        testTask = taskRepository.save(TestDataFactory.createTask("Test Task", testProject, testUser));
    }

    @Test
    void testGetAllTasks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTaskById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks/" + testTask.getId()))
                .andExpect(status().isOk());
    }
}
