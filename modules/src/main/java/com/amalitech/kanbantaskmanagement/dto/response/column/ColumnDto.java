package com.amalitech.kanbantaskmanagement.dto.response.column;

import com.amalitech.kanbantaskmanagement.dto.response.task.TaskDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ColumnDto(
        UUID id,
        String name,
        Double position,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<TaskDto> tasks
) {
}