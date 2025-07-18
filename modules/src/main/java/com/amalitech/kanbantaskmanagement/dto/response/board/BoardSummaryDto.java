package com.amalitech.kanbantaskmanagement.dto.response.board;

import java.util.UUID;
import java.time.LocalDateTime;

public record BoardSummaryDto(
        UUID id,
        String name,
        UUID ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
