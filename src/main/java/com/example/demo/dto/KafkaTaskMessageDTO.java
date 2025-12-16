package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Kafka task messages
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaTaskMessageDTO {
    
    private UUID messageId;
    private String eventType;
    private Long taskId;
    private String taskTitle;
    private String taskStatus;
    private Long userId;
    private String userName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String additionalInfo;
    
    // Default constructor
    public KafkaTaskMessageDTO() {
        this.messageId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor for creating messages
    public KafkaTaskMessageDTO(String eventType, Long taskId, String taskTitle, 
                               String taskStatus, Long userId, String userName) {
        this();
        this.eventType = eventType;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskStatus = taskStatus;
        this.userId = userId;
        this.userName = userName;
    }
    
    // Getters and Setters
    public UUID getMessageId() {
        return messageId;
    }
    
    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskTitle() {
        return taskTitle;
    }
    
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    
    public String getTaskStatus() {
        return taskStatus;
    }
    
    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
    @Override
    public String toString() {
        return "KafkaTaskMessageDTO{" +
                "messageId=" + messageId +
                ", eventType='" + eventType + '\'' +
                ", taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", timestamp=" + timestamp +
                ", additionalInfo='" + additionalInfo + '\'' +
                '}';
    }
}
