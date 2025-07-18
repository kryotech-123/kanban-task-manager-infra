package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.dto.request.column.ColumnRequestDto;
import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;
import com.amalitech.kanbantaskmanagement.exception.BoardNotFoundException;
import com.amalitech.kanbantaskmanagement.exception.ColumnNotFoundException;
import com.amalitech.kanbantaskmanagement.exception.ResourceNotFoundException;
import com.amalitech.kanbantaskmanagement.mapper.ColumnMapper;
import com.amalitech.kanbantaskmanagement.model.jpa.Board;
import com.amalitech.kanbantaskmanagement.model.jpa.Columns;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.ColumnRepository;
import com.amalitech.kanbantaskmanagement.service.ColumnService;
import com.amalitech.kanbantaskmanagement.util.BoardPermissionValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ColumnServiceImpl implements ColumnService{

    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final ColumnMapper columnMapper;
    private final BoardPermissionValidator boardPermissionValidator;

    /**
     * Creates columns from a list of names and assigns them to a given board.
     * Each new column will be given a unique increasing position.
     *
     * @param columnNames List of column names
     * @param boardId ID of the board to which columns belong
     * @return List of ColumnDto for all columns on the board after creation
     */
    @Transactional
    public List<ColumnDto> createColumnsInBatch(List<String> columnNames, UUID boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        double basePosition = columnRepository.findMaxPositionByBoardId(boardId).orElse(0.0) + 1;

        List<Columns> columnsToSave = new ArrayList<>();
        for (String name : columnNames) {
            Columns column = Columns.builder()
                    .name(name.toUpperCase().trim())
                    .board(board)
                    .position(basePosition++)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            columnsToSave.add(column);
        }

        columnRepository.saveAll(columnsToSave);

        List<Columns> updatedColumns = columnRepository.findAllByBoardId(boardId);

        return updatedColumns.stream()
                .map(columnMapper::toColumnDto)
                .toList();
    }

    @Override
    public ColumnDto createSingleColumn(UUID boardId, ColumnRequestDto request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found"));

        Double maxPosition = columnRepository.findMaxPositionByBoardId(boardId).orElse(0.0);
        Columns column = Columns.builder()
                .name(request.name())
                .board(board)
                .position(maxPosition + 1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Columns saved = columnRepository.save(column);
        return columnMapper.toColumnDto(saved);
    }

    @Override
    public ColumnDto updateColumn(UUID columnId, ColumnRequestDto request) {
        Columns column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ColumnNotFoundException("Column not found"));

        column.setName(request.name());
        column.setUpdatedAt(LocalDateTime.now());

        Columns saved = columnRepository.save(column);

        return columnMapper.toColumnDto(saved);
    }


    @Override
    @Transactional
    public void deleteColumn(UUID columnId) {
        columnRepository.findById(columnId).ifPresent(columnRepository::delete);
    }

    @Override
    public ColumnDto getColumn(UUID columnId) {
        Columns column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column", "id", columnId.toString()));
        return columnMapper.toColumnDto(column);
    }

    @Override
    public List<ColumnDto> getColumnsForBoard(UUID boardId) {
        List<Columns> columns = columnRepository.findAllByBoardIdOrderByPositionAsc(boardId);
        return columns.stream().map(columnMapper::toColumnDto).toList();
    }
}