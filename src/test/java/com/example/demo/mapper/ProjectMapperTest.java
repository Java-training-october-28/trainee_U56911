package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {
    
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    
    @Test
    void shouldMapProjectToDTO() {
        // Given
        User owner = new User("owner", "owner@example.com", "password", Role.USER);
        owner.setId(1L);
        
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus("ACTIVE");
        project.setStartDate(LocalDateTime.now());
        project.setEndDate(LocalDateTime.now().plusDays(30));
        project.setCreatedAt(LocalDateTime.now());
        project.setOwner(owner);
        
        // When
        ProjectDTO projectDTO = projectMapper.toDTO(project);
        
        // Then
        assertNotNull(projectDTO);
        assertEquals(project.getId(), projectDTO.getId());
        assertEquals(project.getName(), projectDTO.getName());
        assertEquals(project.getDescription(), projectDTO.getDescription());
        assertEquals(project.getStatus(), projectDTO.getStatus());
        assertEquals(project.getStartDate(), projectDTO.getStartDate());
        assertEquals(project.getEndDate(), projectDTO.getEndDate());
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
            "PLANNING", 
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
        assertEquals(createDTO.getStatus(), project.getStatus());
        assertEquals(createDTO.getStartDate(), project.getStartDate());
        assertEquals(createDTO.getEndDate(), project.getEndDate());
    }
}
