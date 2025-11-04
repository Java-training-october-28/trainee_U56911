package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides basic CRUD operations and custom query methods
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email (unique field)
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find users by role
     */
    List<User> findByRole(Role role);
    
    /**
     * Find users by username containing (case insensitive)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * Find users by email containing (case insensitive)
     */
    List<User> findByEmailContainingIgnoreCase(String email);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Custom query to find users by role with specific criteria
     */
    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.username")
    List<User> findUsersByRoleOrderedByUsername(@Param("role") Role role);
    
    /**
     * Find active users (example of business logic in repository)
     */
    @Query("SELECT u FROM User u WHERE u.email IS NOT NULL AND u.email != ''")
    List<User> findActiveUsers();
}