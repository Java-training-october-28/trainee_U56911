package com.example.demo.service;

import com.example.demo.dto.ProjectCreateDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.dto.ProjectUpdateDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.demo.exception.ResourceNotFoundException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_withOwnerId_savesAndReturnsDTO() {
        Long ownerId = 10L;
        ProjectCreateDTO createDTO = new ProjectCreateDTO();
        createDTO.setName("Test Project");
        createDTO.setDescription("desc");

        Project entity = new Project("Test Project", "desc", null);
        Project saved = new Project("Test Project", "desc", null);
        saved.setId(1L);

        User owner = new User("owner", "o@mail.com", "password", Role.USER);
        owner.setId(ownerId);

        ProjectDTO expectedDTO = new ProjectDTO();
        expectedDTO.setId(1L);
        expectedDTO.setName("Test Project");

        when(projectMapper.toEntity(any(ProjectCreateDTO.class))).thenReturn(entity);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(projectRepository.save(entity)).thenReturn(saved);
        when(projectMapper.toDTO(saved)).thenReturn(expectedDTO);

        ProjectDTO result = projectService.createProject(createDTO, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(projectRepository).save(entity);
    }

    @Test
    void getProjectById_found_returnsDTO() {
        Project project = new Project("P", "d", null);
        project.setId(5L);
        ProjectDTO dto = new ProjectDTO();
        dto.setId(5L);

        when(projectRepository.findById(5L)).thenReturn(Optional.of(project));
        when(projectMapper.toDTO(project)).thenReturn(dto);

        ProjectDTO res = projectService.getProjectById(5L);
        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(5L);
    }

    @Test
    void getProjectsByOwner_returnsList() {
        Project p1 = new Project("A", "a", null);
        Project p2 = new Project("B", "b", null);
        List<Project> list = List.of(p1, p2);

        ProjectDTO d1 = new ProjectDTO();
        ProjectDTO d2 = new ProjectDTO();

        when(projectRepository.findByOwnerId(2L)).thenReturn(list);
        when(projectMapper.toDTOList(list)).thenReturn(List.of(d1, d2));

        List<ProjectDTO> out = projectService.getProjectsByOwner(2L);
        assertThat(out).hasSize(2);
    }

    @Test
    void updateProject_withOwnerChange_updatesAndReturns() {
        // Setup: Create existing project and new owner
        Long projectId = 7L;
        Long newOwnerId = 99L;
        
        Project existing = new Project("Old Project", "Old Description", null);
        existing.setId(projectId);
        existing.setName("Old Project");
        existing.setDescription("Old Description");
        
        User newOwner = new User("newOwner", "newowner@mail.com", "password", Role.USER);
        newOwner.setId(newOwnerId);
        newOwner.setEmail("newowner@mail.com");
        newOwner.setUsername("newOwner");

        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        updateDTO.setOwnerId(newOwnerId);

        ProjectDTO expectedDTO = new ProjectDTO();
        expectedDTO.setId(projectId);
        expectedDTO.setName("Old Project");

        // Mock the repository and mapper interactions
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(userRepository.findById(newOwnerId)).thenReturn(Optional.of(newOwner));
        doAnswer(invocation -> {
            Project project = invocation.getArgument(1);
            // Simulate mapper updating the project with DTO data
            // Since updateDTO only has ownerId, other fields remain unchanged
            return null; // void method
        }).when(projectMapper).updateEntityFromDTO(eq(updateDTO), any(Project.class));
        when(projectRepository.save(existing)).thenReturn(existing);
        when(projectMapper.toDTO(existing)).thenReturn(expectedDTO);

        // Execute the service method
        ProjectDTO result = projectService.updateProject(projectId, updateDTO);

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);

        // Verify that the owner was actually changed on the existing project
        assertThat(existing.getOwner()).isNotNull();
        assertThat(existing.getOwner().getId()).isEqualTo(newOwnerId);
        assertThat(existing.getOwner().getEmail()).isEqualTo("newowner@mail.com");

        // Verify all expected method calls were made
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(newOwnerId);
        verify(projectMapper).updateEntityFromDTO(updateDTO, existing);
        verify(projectRepository).save(existing);
        verify(projectMapper).toDTO(existing);
    }

    @Test
    void createProject_ownerNotFound_throws() {
        Long ownerId = 55L;
        ProjectCreateDTO createDTO = new ProjectCreateDTO();
        createDTO.setName("X");

        when(projectMapper.toEntity(any(ProjectCreateDTO.class))).thenReturn(new Project("X","x",null));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.createProject(createDTO, ownerId));
    }

    @Test
    void getProjectById_notFound_throws() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(999L));
    }

    @Test
    void updateProject_projectNotFound_throws() {
        Long pid = 123L;
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        when(projectRepository.findById(pid)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(pid, updateDTO));
    }

    @Test
    void updateProject_ownerNotFound_throws() {
        Long projectId = 8L;
        Project existing = new Project("O","o",null);
        existing.setId(projectId);
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO();
        updateDTO.setOwnerId(77L);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(projectId, updateDTO));
    }
}
