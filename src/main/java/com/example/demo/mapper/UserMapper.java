package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, TaskMapper.class, CommentMapper.class})
public interface UserMapper {
    
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    UserDTO toDTO(User user);
    
    @Mapping(target = "ownedProjects", source = "ownedProjects")
    @Mapping(target = "assignedTasks", source = "assignedTasks")
    @Mapping(target = "comments", source = "comments")
    UserDTO toDTOWithRelations(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    User toEntity(UserCreateDTO userCreateDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
    
    List<UserDTO> toDTOList(List<User> users);
}
