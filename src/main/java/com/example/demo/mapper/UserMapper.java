package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Named("basicUserDTO")
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    UserDTO toDTO(User user);

    @Named("userDTOWithRelations")
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    UserDTO toDTOWithRelations(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastFailedLogin", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toEntity(UserCreateDTO userCreateDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastFailedLogin", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
    
    List<UserDTO> toDTOList(List<User> users);
}
