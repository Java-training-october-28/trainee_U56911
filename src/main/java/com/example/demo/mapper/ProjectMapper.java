package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Project;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {
    
    @Named("basicProjectDTO")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "owner", qualifiedByName = "basicUserDTO")
    ProjectDTO toDTO(Project project);

    @Named("projectDTOWithTasks")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "owner", qualifiedByName = "basicUserDTO")
    ProjectDTO toDTOWithTasks(Project project);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toEntity(ProjectCreateDTO projectCreateDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProjectUpdateDTO projectUpdateDTO, @MappingTarget Project project);
    
    List<ProjectDTO> toDTOList(List<Project> projects);
}
