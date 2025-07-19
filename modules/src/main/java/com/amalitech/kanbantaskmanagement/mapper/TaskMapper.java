package com.amalitech.kanbantaskmanagement.mapper;

import com.amalitech.kanbantaskmanagement.dto.response.subtask.SubtaskDto;
import com.amalitech.kanbantaskmanagement.dto.response.task.TaskDto;
import com.amalitech.kanbantaskmanagement.model.jpa.Task;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for Task entity and its DTO.
 * Handles conversion between Task entity and TaskDto.
 */
@Component
public class TaskMapper {

    private final SubtaskMapper subtaskMapper;
    private final UserRepository userRepository;

    public TaskMapper(SubtaskMapper subtaskMapper, UserRepository userRepository) {
        this.subtaskMapper = subtaskMapper;
        this.userRepository = userRepository;
    }

    /**
     * Converts a Task entity to a TaskDto.
     * This method expects associated collections (subtasks) to be initialized
     * (either eagerly fetched or accessed within a transactional context).
     *
     * @param task The Task entity to convert.
     * @return The corresponding TaskDto.
     */
    public TaskDto toTaskDto(Task task) {
        UUID assigneeId = null;
        String assigneeUsername = null;
        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
            assigneeUsername = task.getAssignee().getUsername();
        }

        List<SubtaskDto> subtaskDtos = task.getSubtasks().stream()
                .map(subtaskMapper::toSubtaskDto)
                .collect(Collectors.toList());

        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                assigneeId,
                assigneeUsername,
                task.getDueDate(),
                task.getPosition(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                subtaskDtos
        );
    }

    /**
     * Converts a TaskDto to a Task entity for creation/update.
     * Note: For creation, the column relationship must be set by a service.
     *
     * @param taskDto The TaskDto to convert.
     * @param existingTask Optional existing Task entity for updates.
     * @return A new or updated Task entity.
     */
    public Task toTaskEntity(TaskDto taskDto, Task existingTask) {
        Task taskEntity = Optional.ofNullable(existingTask).orElseGet(Task::new);

        taskEntity.setTitle(taskDto.title());
        taskEntity.setDescription(taskDto.description());
        taskEntity.setDueDate(taskDto.dueDate());
        taskEntity.setPosition(taskDto.position());

        if (taskDto.assigneeId() != null) {
            User assignee = userRepository.findById(taskDto.assigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee user not found with ID: " + taskDto.assigneeId()));
            taskEntity.setAssignee(assignee);
        } else {
            taskEntity.setAssignee(null);
        }

        return taskEntity;
    }
}
