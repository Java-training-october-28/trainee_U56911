package com.example.demo.service;

import com.example.demo.dto.ProjectCreateDTO;
import com.example.demo.dto.ProjectDTO;
import com.example.demo.dto.ProjectUpdateDTO;

import java.util.List;

public interface ProjectServiceInterface {
    ProjectDTO createProject(ProjectCreateDTO createDTO, Long ownerId);

    ProjectDTO getProjectById(Long id);

    List<ProjectDTO> getProjectsByOwner(Long ownerId);

    ProjectDTO updateProject(Long id, ProjectUpdateDTO updateDTO);
}
