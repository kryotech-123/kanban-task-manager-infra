package com.amalitech.kanbantaskmanagement.dto.response.collaborator;

import com.amalitech.kanbantaskmanagement.model.jpa.enums.BoardPermission;

import java.time.LocalDateTime;
import java.util.UUID;

public record BoardCollaboratorDto(
        UUID id,
        UUID userId,
        String username,
        UUID boardId,
        BoardPermission permission,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}