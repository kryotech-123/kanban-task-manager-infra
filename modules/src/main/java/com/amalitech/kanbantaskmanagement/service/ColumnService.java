package com.amalitech.kanbantaskmanagement.service;

import com.amalitech.kanbantaskmanagement.dto.request.column.ColumnRequestDto;
import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;

import java.util.List;
import java.util.UUID;

public interface ColumnService {
    List<ColumnDto> createColumnsInBatch(List<String> columnNames, UUID boardId);
    ColumnDto createSingleColumn(UUID boardId, ColumnRequestDto request);
    ColumnDto updateColumn(UUID columnId, ColumnRequestDto request);
    void deleteColumn(UUID columnId);
    ColumnDto getColumn(UUID columnId);
    List<ColumnDto> getColumnsForBoard(UUID boardId);
}

