package com.example.demo.entity;

/**
 * Task-related notifications
 */
public final record TaskNotification(
    String message,
    Long taskId,
    String taskTitle,
    NotificationPriority priority
) implements NotificationType {
    public TaskNotification {
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
