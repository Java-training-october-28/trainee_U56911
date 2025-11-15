package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.Priority;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, UserMapper.class})
public interface TaskMapper {
            @Named("priorityToString")
            default String priorityToString(Priority priority) {
                if (priority == null) return null;
                switch (priority) {
                    case LOW: return "LOW";
                    case MEDIUM: return "MEDIUM";
                    case HIGH: return "HIGH";
                    case CRITICAL: return "URGENT";
                    default: throw new IllegalArgumentException("Unknown Priority: " + priority);
                }
            }

            @Named("stringToPriority")
            default Priority stringToPriority(String priority) {
                if (priority == null) return null;
                switch (priority) {
                    case "LOW": return Priority.LOW;
                    case "MEDIUM": return Priority.MEDIUM;
                    case "HIGH": return Priority.HIGH;
                    case "URGENT": return Priority.CRITICAL;
                    default: throw new IllegalArgumentException("Unknown priority string: " + priority);
                }
            }
        @Named("taskPriorityToPriority")
        default Priority taskPriorityToPriority(TaskPriority taskPriority) {
            if (taskPriority == null) return null;
            switch (taskPriority) {
                case LOW: return Priority.LOW;
                case MEDIUM: return Priority.MEDIUM;
                case HIGH: return Priority.HIGH;
                case URGENT: return Priority.CRITICAL;
                default: throw new IllegalArgumentException("Unknown TaskPriority: " + taskPriority);
            }
        }
    
    @Named("basicTaskDTO")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "project", qualifiedByName = "basicProjectDTO")
    @Mapping(target = "assignee", qualifiedByName = "basicUserDTO")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityToString")
    TaskDTO toDTO(Task task);

    @Named("taskDTOWithComments")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "project", qualifiedByName = "basicProjectDTO")
    @Mapping(target = "assignee", qualifiedByName = "basicUserDTO")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityToString")
    TaskDTO toDTOWithComments(Task task);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "priority", source = "priority", qualifiedByName = "taskPriorityToPriority")
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
