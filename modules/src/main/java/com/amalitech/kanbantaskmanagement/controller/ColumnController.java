package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.dto.request.column.ColumnRequestDto;
import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;
import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;
import com.amalitech.kanbantaskmanagement.exception.ResourceNotFoundException;
import com.amalitech.kanbantaskmanagement.service.ColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/board/{boardId}/column")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @PostMapping
    public ResponseEntity<ApiResponse<ColumnDto>> createColumn(
            @PathVariable UUID boardId,
            @Valid @RequestBody ColumnRequestDto request) {
        ColumnDto column = columnService.createSingleColumn(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(column, "Column created successfully"));
    }



    @PutMapping("/{columnId}")
    public ResponseEntity<ApiResponse<ColumnDto>> updateColumn(
            @PathVariable UUID columnId,
            @Valid @RequestBody ColumnRequestDto request) {
        ColumnDto updated = columnService.updateColumn(columnId, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Column updated successfully"));
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<ApiResponse<Void>> deleteColumn(@PathVariable UUID columnId) {
        columnService.deleteColumn(columnId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Column deleted successfully"));
    }

    @GetMapping("/{columnId}")
    public ResponseEntity<ApiResponse<ColumnDto>> getColumn(@PathVariable UUID columnId) {
        try {
            ColumnDto column = columnService.getColumn(columnId);
            return ResponseEntity.ok(ApiResponse.success(column, "Column fetched successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Column not found with ID: " + columnId));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ColumnDto>>> getBoardColumns(@PathVariable UUID boardId) {
        List<ColumnDto> columns = columnService.getColumnsForBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(columns, "Board columns fetched successfully"));
    }
}
