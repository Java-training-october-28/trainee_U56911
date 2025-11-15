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
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    private User testUser;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("testuser", "test@email.com", "password", Role.USER));
        testProject = projectRepository.save(new Project("Test Project", "Description", testUser));
    }

    @Test
    void testGetAllProjects() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/" + testProject.getId()))
                .andExpect(status().isOk());
    }
}
