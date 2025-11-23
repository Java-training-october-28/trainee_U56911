package com.example.demo.entity;

/**
 * Sealed interface for notification types hierarchy
 * Demonstrates Java 21 sealed classes feature
 */
public sealed interface NotificationType 
    permits TaskNotification, ProjectNotification, SystemNotification {
    
    String getMessage();
    NotificationPriority getPriority();
}
