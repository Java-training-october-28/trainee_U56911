package com.example.demo.factory;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SampleDataTest {
    @Test
    void testSampleUserFactory() {
        User user = TestDataFactory.createUser("sample");
        assertEquals("sample", user.getUsername());
        assertEquals("sample@example.com", user.getEmail());
    }

    @Test
    void testSampleProjectFactory() {
        User user = TestDataFactory.createUser("owner");
        Project project = TestDataFactory.createProject("proj", user);
        assertEquals("proj", project.getName());
        assertEquals(user, project.getOwner());
    }

    @Test
    void testSampleTaskFactory() {
        User user = TestDataFactory.createUser("assignee");
        Project project = TestDataFactory.createProject("proj", user);
        Task task = TestDataFactory.createTask("task", project, user);
        assertEquals("task", task.getName());
        assertEquals(project, task.getProject());
        assertEquals(user, task.getAssignee());
    }
}
