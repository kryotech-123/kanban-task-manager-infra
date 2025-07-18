package com.amalitech.kanbantaskmanagement.dto.response.board;

import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;
import com.amalitech.kanbantaskmanagement.dto.response.collaborator.BoardCollaboratorDto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public record BoardDetailDto(
        UUID id,
        String name,
        UUID ownerId,
        String ownerUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ColumnDto> columns,
        List<BoardCollaboratorDto> collaborators
) {
}
