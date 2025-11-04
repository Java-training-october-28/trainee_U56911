package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    
    @Test
    void shouldMapUserToDTO() {
        // Given
        User user = new User("testuser", "test@example.com", "password123", Role.USER);
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());
        
        // When
        UserDTO userDTO = userMapper.toDTO(user);
        
        // Then
        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getRole(), userDTO.getRole());
        assertEquals(user.getCreatedAt(), userDTO.getCreatedAt());
    }
    
    @Test
    void shouldMapUserCreateDTOToEntity() {
        // Given
        UserCreateDTO userCreateDTO = new UserCreateDTO("newuser", "new@example.com", "password123", Role.USER);
        
        // When
        User user = userMapper.toEntity(userCreateDTO);
        
        // Then
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertEquals(userCreateDTO.getUsername(), user.getUsername());
        assertEquals(userCreateDTO.getEmail(), user.getEmail());
        assertEquals(userCreateDTO.getPassword(), user.getPassword());
        assertEquals(userCreateDTO.getRole(), user.getRole());
    }
    
    @Test
    void shouldUpdateEntityFromDTO() {
        // Given
        User existingUser = new User("olduser", "old@example.com", "oldpassword", Role.USER);
        existingUser.setId(1L);
        existingUser.setCreatedAt(LocalDateTime.now());
        
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setUsername("updateduser");
        updateDTO.setEmail("updated@example.com");
        
        // When
        userMapper.updateEntityFromDTO(updateDTO, existingUser);
        
        // Then
        assertEquals("updateduser", existingUser.getUsername());
        assertEquals("updated@example.com", existingUser.getEmail());
        assertEquals("oldpassword", existingUser.getPassword()); // Should remain unchanged
        assertEquals(Role.USER, existingUser.getRole()); // Should remain unchanged
        assertNotNull(existingUser.getId()); // Should remain unchanged
        assertNotNull(existingUser.getCreatedAt()); // Should remain unchanged
    }
}
