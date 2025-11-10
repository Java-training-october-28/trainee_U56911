package com.example.demo.mapper;

import com.example.demo.dto.TaskUpdateDTO;
import com.example.demo.entity.Task;
import com.example.demo.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskMapperTest {
    
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    
    @Test
    void shouldUpdateOnlyProvidedFields() {
        // Given - existing task with all fields populated
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus("TODO");
        existingTask.setPriority("HIGH");
        existingTask.setDueDate(LocalDateTime.of(2024, 1, 1, 10, 0));
        existingTask.setCreatedAt(LocalDateTime.of(2023, 12, 1, 9, 0));
        
        // When - update with partial data
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        updateDTO.setTitle("Updated Title");      // Only update title
        updateDTO.setStatus("IN_PROGRESS");       // Only update status
        // description, priority, dueDate are null
        
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        
        // Then - only specified fields should be updated
        assertEquals("Updated Title", existingTask.getTitle());           // ✅ Updated
        assertEquals("IN_PROGRESS", existingTask.getStatus());            // ✅ Updated
        assertEquals("Original Description", existingTask.getDescription()); // ✅ Preserved
        assertEquals("HIGH", existingTask.getPriority());                 // ✅ Preserved
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), existingTask.getDueDate()); // ✅ Preserved
        
        // Critical fields should never change
        assertEquals(1L, existingTask.getId());                          // ✅ Preserved
        assertEquals(LocalDateTime.of(2023, 12, 1, 9, 0), existingTask.getCreatedAt()); // ✅ Preserved
    }
    
    @Test
    void shouldIgnoreNullValues() {
        // Given
        Task existingTask = new Task();
        existingTask.setTitle("Keep This Title");
        existingTask.setDescription("Keep This Description");
        
        // When - all fields in DTO are null
        TaskUpdateDTO updateDTO = new TaskUpdateDTO();
        // All fields are null
        
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        
        // Then - nothing should change
        assertEquals("Keep This Title", existingTask.getTitle());
        assertEquals("Keep This Description", existingTask.getDescription());
    }
}
