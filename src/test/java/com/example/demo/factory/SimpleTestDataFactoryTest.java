package com.example.demo.factory;

import com.example.demo.entity.*;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify the enhanced TestDataFactory works correctly.
 * This test bypasses the problematic test files with missing dependencies.
 */
public class SimpleTestDataFactoryTest {
    
    @Test
    void testBasicUserCreation() {
        User user = TestDataFactory.createUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("testuser@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
    }
    
    @Test
    void testAdminUserCreation() {
        User admin = TestDataFactory.createAdminUser("adminuser");
        assertNotNull(admin);
        assertEquals("adminuser", admin.getUsername());
        assertEquals(Role.ADMIN, admin.getRole());
    }
    
    @Test
    void testBulkUserCreation() {
        List<User> users = TestDataFactory.createUsers(5);
        assertNotNull(users);
        assertEquals(5, users.size());
        
        for (int i = 0; i < 5; i++) {
            assertEquals("user" + i, users.get(i).getUsername());
            assertEquals("user" + i + "@example.com", users.get(i).getEmail());
        }
    }
    
    @Test
    void testProjectWithTasks() {
        User owner = TestDataFactory.createUser("owner");
        Project project = TestDataFactory.createProjectWithTasks("TestProject", owner, 3);
        
        assertNotNull(project);
        assertEquals("TestProject", project.getName());
        assertEquals(owner, project.getOwner());
        assertNotNull(project.getTasks());
        assertEquals(3, project.getTasks().size());
    }
    
    @Test
    void testRandomUserCreation() {
        User randomUser = TestDataFactory.createRandomUser();
        assertNotNull(randomUser);
        assertNotNull(randomUser.getUsername());
        assertNotNull(randomUser.getEmail());
        assertTrue(randomUser.getUsername().startsWith("user_"));
        assertTrue(randomUser.getEmail().contains("@example.com"));
    }
    
    @Test
    void testInvalidUserCreation() {
        User invalidUser = TestDataFactory.createInvalidUser();
        assertNotNull(invalidUser);
        assertEquals("", invalidUser.getUsername());
        assertEquals("", invalidUser.getEmail());
    }
    
    @Test
    void testPersistedUserCreation() {
        User persistedUser = TestDataFactory.createPersistedUser(999L);
        assertNotNull(persistedUser);
        assertEquals(Long.valueOf(999L), persistedUser.getId());
        assertEquals("user_999", persistedUser.getUsername());
    }
    
    @Test
    void testValidUserWithAllFields() {
        User validUser = TestDataFactory.createValidUserWithAllFields();
        assertNotNull(validUser);
        assertEquals("valid_user", validUser.getUsername());
        assertEquals("valid@example.com", validUser.getEmail());
        assertEquals("validpassword123", validUser.getPassword());
        assertEquals(Role.USER, validUser.getRole());
        assertNotNull(validUser.getCreatedAt());
        assertTrue(validUser.getIsActive());
        assertFalse(validUser.getEmailVerified());
    }
}
