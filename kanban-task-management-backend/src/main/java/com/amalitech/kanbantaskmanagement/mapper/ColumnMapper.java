package com.amalitech.kanbantaskmanagement.mapper;

import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;
import com.amalitech.kanbantaskmanagement.dto.response.task.TaskDto;
import com.amalitech.kanbantaskmanagement.model.jpa.Columns;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper for Column entity and its DTO.
 * Handles conversion between Column entity and ColumnDto.
 */
@Component
public class ColumnMapper {

    private final TaskMapper taskMapper; // Dependency to map nested tasks

    public ColumnMapper(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    /**
     * Converts a Column entity to a ColumnDto.
     * This method expects associated collections (tasks) to be initialized
     * (either eagerly fetched or accessed within a transactional context).
     *
     * @param columns The Column entity to convert.
     * @return The corresponding ColumnDto.
     */
    public ColumnDto toColumnDto(Columns columns) {
        List<TaskDto> taskDtos = columns.getTasks().stream()
                .map(taskMapper::toTaskDto)
                .sorted(Comparator.comparing(TaskDto::position).reversed())
                .collect(Collectors.toList());

        return new ColumnDto(
                columns.getId(),
                columns.getName(),
                columns.getPosition(),
                columns.getCreatedAt(),
                columns.getUpdatedAt(),
                taskDtos
        );
    }

    /**
     * Converts a ColumnDto to a Column entity for creation/update.
     * Note: For creation, the board relationship must be set by a service.
     *
     * @param columnDto The ColumnDto to convert.
     * @param existingColumns Optional existing Column entity for updates.
     * @return A new or updated Column entity.
     */
    public Columns toColumnEntity(ColumnDto columnDto, Columns existingColumns) {
        Columns columnEntity = Optional.ofNullable(existingColumns).orElseGet(Columns::new);

        columnEntity.setName(columnDto.name());
        columnEntity.setPosition(columnDto.position());

        return columnEntity;
    }
}

