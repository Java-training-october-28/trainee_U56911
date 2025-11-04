package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Task;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, UserMapper.class, CommentMapper.class})
public interface TaskMapper {
    
    @Mapping(target = "comments", ignore = true)
    TaskDTO toDTO(Task task);
    
    @Mapping(target = "comments", source = "comments")
    TaskDTO toDTOWithComments(Task task);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task toEntity(TaskCreateDTO taskCreateDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TaskUpdateDTO taskUpdateDTO, @MappingTarget Task task);
    
    List<TaskDTO> toDTOList(List<Task> tasks);
}
