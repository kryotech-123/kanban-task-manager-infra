package com.amalitech.kanbantaskmanagement.dto.response.subtask;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubtaskDto(
        UUID id,
        UUID taskId,
        String title,
        String description,
        boolean isCompleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
