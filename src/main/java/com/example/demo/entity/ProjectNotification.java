package com.example.demo.entity;

/**
 * Project-related notifications
 */
public final record ProjectNotification(
    String message,
    Long projectId,
    String projectName,
    NotificationPriority priority
) implements NotificationType {
    public ProjectNotification {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    @Override
    public NotificationPriority getPriority() {
        return priority;
    }
}
