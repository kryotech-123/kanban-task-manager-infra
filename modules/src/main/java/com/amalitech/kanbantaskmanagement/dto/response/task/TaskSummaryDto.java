package com.amalitech.kanbantaskmanagement.dto.response.task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskSummaryDto(
        UUID id,
        String title,
        String description,
        UUID assigneeId,
        String assigneeUsername,
        LocalDateTime dueDate
) {
}
