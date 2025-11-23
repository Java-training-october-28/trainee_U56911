package com.example.demo.service;

import com.example.demo.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service demonstrating Java 21 pattern matching and switch expressions
 */
@Service
public class NotificationService {

    /**
     * Demonstrates pattern matching with switch expressions
     * Processes different notification types using modern Java features
     */
    public String processNotification(NotificationType notification) {
        // Pattern matching with switch expressions
        return switch (notification) {
            case TaskNotification task -> {
                String message = "Task Notification: " + task.message() + "\n" +
                                "Task ID: " + task.taskId() + "\n" +
                                "Priority: " + task.priority();
                yield message;
            }
            case ProjectNotification project -> {
                String message = "Project Notification: " + project.message() + "\n" +
                                "Project: " + project.projectName() + "\n" +
                                "Priority: " + project.priority();
                yield message;
            }
            case SystemNotification system -> {
                String message = "System Notification: " + system.message() + "\n" +
                                "Component: " + system.systemComponent() + "\n" +
                                "Priority: " + system.priority();
                yield message;
            }
            // No default needed - sealed interface ensures exhaustive coverage
        };
    }

    /**
     * Demonstrates pattern matching in if statements
     */
    public boolean isHighPriorityNotification(NotificationType notification) {
        // Pattern matching in if statement
        if (notification instanceof TaskNotification task && 
            task.priority() == NotificationPriority.HIGH) {
            return true;
        }
        if (notification instanceof ProjectNotification project && 
            project.priority() == NotificationPriority.HIGH) {
            return true;
        }
        if (notification instanceof SystemNotification system && 
            system.priority() == NotificationPriority.HIGH) {
            return true;
        }
        return false;
    }

    /**
     * Demonstrates enhanced switch with multiple patterns
     */
    public String getNotificationCategory(NotificationType notification) {
        return switch (notification) {
            case TaskNotification task -> "TASK";
            case ProjectNotification project -> "PROJECT";
            case SystemNotification system -> "SYSTEM";
        };
    }

    /**
     * Demonstrates pattern matching with filtering
     */
    public List<String> getHighPriorityMessages(List<NotificationType> notifications) {
        return notifications.stream()
            .filter(notification -> {
                // Pattern matching with filtering
                return switch (notification) {
                    case TaskNotification task -> 
                        task.priority() == NotificationPriority.HIGH || 
                        task.priority() == NotificationPriority.CRITICAL;
                    case ProjectNotification project -> 
                        project.priority() == NotificationPriority.HIGH || 
                        project.priority() == NotificationPriority.CRITICAL;
                    case SystemNotification system -> 
                        system.priority() == NotificationPriority.HIGH || 
                        system.priority() == NotificationPriority.CRITICAL;
                };
            })
            .map(NotificationType::getMessage)
            .collect(Collectors.toList());
    }

    /**
     * Demonstrates complex pattern matching with nested conditions
     */
    public String getNotificationAction(NotificationType notification) {
        return switch (notification) {
            case TaskNotification task when task.priority() == NotificationPriority.CRITICAL -> 
                "IMMEDIATE_ACTION_REQUIRED";
            case TaskNotification task when task.priority() == NotificationPriority.HIGH -> 
                "URGENT_REVIEW";
            case TaskNotification task -> "STANDARD_REVIEW";
            case ProjectNotification project when project.priority() == NotificationPriority.CRITICAL -> 
                "ESCALATE_TO_MANAGER";
            case ProjectNotification project -> "TEAM_REVIEW";
            case SystemNotification system when system.priority() == NotificationPriority.CRITICAL -> 
                "SYSTEM_ADMIN_ALERT";
            case SystemNotification system -> "SYSTEM_MONITORING";
        };
    }
}
