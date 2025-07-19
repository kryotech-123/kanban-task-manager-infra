package com.amalitech.kanbantaskmanagement.mapper;

import com.amalitech.kanbantaskmanagement.dto.response.subtask.SubtaskDto;
import com.amalitech.kanbantaskmanagement.model.jpa.Subtask;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper for Subtask entity and its DTO.
 * Handles conversion between Subtask entity and SubtaskDto.
 */
@Component
public class SubtaskMapper {

    /**
     * Converts a Subtask entity to a SubtaskDto.
     *
     * @param subtask The Subtask entity to convert.
     * @return The corresponding SubtaskDto.
     */
    public SubtaskDto toSubtaskDto(Subtask subtask) {
        return new SubtaskDto(
                subtask.getId(),
                subtask.getTask().getId(),
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.isCompleted(),
                subtask.getCreatedAt(),
                subtask.getUpdatedAt()
        );
    }

    /**
     * Converts a SubtaskDto to a Subtask entity for creation/update.
     * Note: The task relationship must be set by a service when creating a new subtask.
     *
     * @param subtaskDto The SubtaskDto to convert.
     * @param existingSubtask Optional existing Subtask entity for updates.
     * @return A new or updated Subtask entity.
     */
    public Subtask toSubtaskEntity(SubtaskDto subtaskDto, Subtask existingSubtask) {
        Subtask subtaskEntity = Optional.ofNullable(existingSubtask).orElseGet(Subtask::new);

        subtaskEntity.setTitle(subtaskDto.title());
        subtaskEntity.setDescription(subtaskDto.description());
        subtaskEntity.setCompleted(subtaskDto.isCompleted());

        return subtaskEntity;
    }
}