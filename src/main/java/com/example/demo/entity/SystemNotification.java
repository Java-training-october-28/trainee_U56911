package com.example.demo.entity;

/**
 * System-level notifications
 */
public final record SystemNotification(
    String message,
    String systemComponent,
    NotificationPriority priority
) implements NotificationType {
    public SystemNotification {
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
