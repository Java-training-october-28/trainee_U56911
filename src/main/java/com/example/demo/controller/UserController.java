package com.example.demo.controller;

import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ResourceAlreadyExistsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for user management")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user account")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "201", description = "User created successfully"),
        @SwaggerApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        // Check if email already exists
        if (userService.existsByEmail(createDTO.getEmail())) {
            throw ResourceAlreadyExistsException.userEmail(createDTO.getEmail());
        }
        
        User user = userMapper.toEntity(createDTO);
        User savedUser = userService.save(user);
        UserDTO responseDTO = userMapper.toDTO(savedUser);
        
        ApiResponse<UserDTO> response = ApiResponse.success(responseDTO, "User created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "User found"),
        @SwaggerApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@Parameter(description = "User ID") @PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw ResourceNotFoundException.user(id);
        }
        
        UserDTO userDTO = userMapper.toDTO(user);
        ApiResponse<UserDTO> response = ApiResponse.success(userDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Users retrieved")
    })
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        
        ApiResponse<List<UserDTO>> response = ApiResponse.success(userDTOs, 
            String.format("Retrieved %d users", userDTOs.size()));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates a user's information")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "User updated successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "User not found"),
        @SwaggerApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id, 
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            throw ResourceNotFoundException.user(id);
        }
        
        // Check if email is being changed and if it already exists
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(existingUser.getEmail())) {
            if (userService.existsByEmail(updateDTO.getEmail())) {
                throw ResourceAlreadyExistsException.userEmail(updateDTO.getEmail());
            }
        }
        
        userMapper.updateEntityFromDTO(updateDTO, existingUser);
        User updatedUser = userService.save(existingUser);
        UserDTO responseDTO = userMapper.toDTO(updatedUser);
        
        ApiResponse<UserDTO> response = ApiResponse.success(responseDTO, "User updated successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "User deleted successfully"),
        @SwaggerApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw ResourceNotFoundException.user(id);
        }
        
        userService.deleteById(id);
        
        ApiResponse<Void> response = ApiResponse.success("User deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieves a user by their email address")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "User found"),
        @SwaggerApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@Parameter(description = "User email") @PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        
        UserDTO userDTO = userMapper.toDTO(user);
        ApiResponse<UserDTO> response = ApiResponse.success(userDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieves all users with a specific role")
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Users retrieved")
    })
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@Parameter(description = "User role") @PathVariable Role role) {
        List<User> users = userService.findByRole(role);
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        
        ApiResponse<List<UserDTO>> response = ApiResponse.success(userDTOs, 
            String.format("Retrieved %d users with role '%s'", userDTOs.size(), role));
        return ResponseEntity.ok(response);
    }
}
