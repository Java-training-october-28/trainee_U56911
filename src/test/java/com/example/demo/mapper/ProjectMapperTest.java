package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectMapperTest {
    
    @Autowired
    private ProjectMapper projectMapper;
    
    @Test
    void shouldMapProjectToDTO() {
        // Given
        User owner = new User("owner", "owner@example.com", "password", Role.USER);
        owner.setId(1L);
        
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setCreatedAt(LocalDateTime.now());
        project.setOwner(owner);
        
        // When
        ProjectDTO projectDTO = projectMapper.toDTO(project);
        
        // Then
        assertNotNull(projectDTO);
        assertEquals(project.getId(), projectDTO.getId());
        assertEquals(project.getName(), projectDTO.getName());
        assertEquals(project.getDescription(), projectDTO.getDescription());
        assertEquals(project.getCreatedAt(), projectDTO.getCreatedAt());
        assertNotNull(projectDTO.getOwner());
    }
    
    @Test
    void shouldMapProjectCreateDTOToEntity() {
        // Given
        ProjectCreateDTO createDTO = new ProjectCreateDTO(
            "New Project", 
            "New Description", 
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30), 
            ProjectStatus.PLANNING, 
            1L
        );
        
        // When
        Project project = projectMapper.toEntity(createDTO);
        
        // Then
        assertNotNull(project);
        assertNull(project.getId());
        assertNull(project.getCreatedAt());
        assertEquals(createDTO.getName(), project.getName());
        assertEquals(createDTO.getDescription(), project.getDescription());
    }
}
