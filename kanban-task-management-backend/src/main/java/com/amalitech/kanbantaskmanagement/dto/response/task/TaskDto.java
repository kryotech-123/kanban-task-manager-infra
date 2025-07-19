package com.amalitech.kanbantaskmanagement.dto.response.task;

import com.amalitech.kanbantaskmanagement.dto.response.subtask.SubtaskDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskDto(
        UUID id,
        String title,
        String description,
        UUID assigneeId,
        String assigneeUsername,
        LocalDateTime dueDate,
        Double position,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<SubtaskDto> subtasks
) {
}
