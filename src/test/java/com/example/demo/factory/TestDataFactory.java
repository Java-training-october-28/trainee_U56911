package com.example.demo.factory;

import com.example.demo.entity.User;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;

public class TestDataFactory {
    public static User createUser(String name) {
        return new User(name, name + "@example.com", "password");
    }

    public static Project createProject(String name, User owner) {
        return new Project(name, name + " description", owner);
    }

    public static Task createTask(String name, Project project, User assignee) {
        return new Task(name, name + " details", project, assignee);
    }
}
