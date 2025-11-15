package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(com.example.demo.config.TestSecurityConfig.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    private User testUser;
    private Project testProject;
    private Task testTask;
    private Comment testComment;
    
    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testuser", "test@email.com", "password", Role.USER));
        testProject = projectRepository.save(new Project("Test Project", "Description", testUser));
        testTask = taskRepository.save(new Task("Test Task", "Task Description", testProject));
        testComment = commentRepository.save(new Comment("Test comment", testTask, testUser));
    }

    @Test
    void testGetAllComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCommentById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/" + testComment.getId()))
                .andExpect(status().isOk());
    }
}
