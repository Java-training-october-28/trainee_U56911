package com.example.demo.service;

import com.example.demo.dto.TaskCreateDTO;
import com.example.demo.dto.TaskDTO;
import com.example.demo.entity.TaskStatus;

import java.util.List;

public interface TaskServiceInterface {
    TaskDTO createTask(TaskCreateDTO createDTO);

    TaskDTO getTaskById(Long id);

    List<TaskDTO> getTasksByProject(Long projectId);

    TaskDTO updateTaskStatus(Long id, TaskStatus status);
}
