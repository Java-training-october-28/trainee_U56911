package com.example.demo.service;

import com.example.demo.dto.TaskCreateDTO;
import com.example.demo.dto.TaskDTO;
import com.example.demo.entity.Project;
import com.example.demo.entity.Task;
import com.example.demo.entity.TaskPriority;
import com.example.demo.entity.TaskStatus;
import com.example.demo.entity.User;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.example.demo.exception.ResourceNotFoundException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_setsRelationshipsAndReturnsDTO() {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("T1");
        dto.setDescription("d");
        dto.setProjectId(11L);
        dto.setAssigneeId(22L);
        dto.setPriority(TaskPriority.MEDIUM);
        dto.setStatus(TaskStatus.TODO);

        Task entity = new Task("T1", "d", null);
        Task saved = new Task("T1", "d", null);
        saved.setId(3L);

        Project project = new Project("P", "d", null);
        project.setId(11L);

        User assignee = new User("a", "a@mail.com", "pw", null);
        assignee.setId(22L);

        TaskDTO expected = new TaskDTO();
        expected.setId(3L);

        when(taskMapper.toEntity(any(TaskCreateDTO.class))).thenReturn(entity);
        when(projectRepository.findById(11L)).thenReturn(Optional.of(project));
        when(userRepository.findById(22L)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(entity)).thenReturn(saved);
        when(taskMapper.toDTO(saved)).thenReturn(expected);

        TaskDTO out = taskService.createTask(dto);
        assertThat(out).isNotNull();
        assertThat(out.getId()).isEqualTo(3L);
        verify(taskRepository).save(entity);
    }

    @Test
    void getTaskById_found_returnsDTO() {
        Task t = new Task("x","y", null);
        t.setId(4L);
        TaskDTO dto = new TaskDTO();
        dto.setId(4L);

        when(taskRepository.findById(4L)).thenReturn(Optional.of(t));
        when(taskMapper.toDTO(t)).thenReturn(dto);

        TaskDTO res = taskService.getTaskById(4L);
        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(4L);
    }

    @Test
    void getTasksByProject_returnsList() {
        Task t1 = new Task("a","b", null);
        Task t2 = new Task("c","d", null);
        List<Task> tasks = List.of(t1, t2);

        when(taskRepository.findByProjectId(7L)).thenReturn(tasks);
        when(taskMapper.toDTOList(tasks)).thenReturn(List.of(new TaskDTO(), new TaskDTO()));

        java.util.List<TaskDTO> out = taskService.getTasksByProject(7L);
        assertThat(out).hasSize(2);
    }

    @Test
    void updateTaskStatus_updatesAndReturnsDTO() {
        Task t = new Task("n","m", null);
        t.setId(8L);
        TaskDTO dto = new TaskDTO();
        dto.setId(8L);

        when(taskRepository.findById(8L)).thenReturn(Optional.of(t));
        when(taskRepository.save(t)).thenReturn(t);
        when(taskMapper.toDTO(t)).thenReturn(dto);

        TaskDTO res = taskService.updateTaskStatus(8L, TaskStatus.IN_PROGRESS);
        assertThat(res).isNotNull();
        verify(taskRepository).save(t);
    }

    @Test
    void createTask_projectNotFound_throws() {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("T1");
        dto.setProjectId(99L);

        when(taskMapper.toEntity(any(TaskCreateDTO.class))).thenReturn(new Task("t","d", null));
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(dto));
    }

    @Test
    void getTaskById_notFound_throws() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void updateTaskStatus_taskNotFound_throws() {
        when(taskRepository.findById(555L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTaskStatus(555L, TaskStatus.COMPLETED));
    }
}
