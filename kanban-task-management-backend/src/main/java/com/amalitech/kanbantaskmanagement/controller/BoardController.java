package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardPartialUpdateDto;
import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardRequestDTO;

import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardDetailDto;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardSummaryDto;
import com.amalitech.kanbantaskmanagement.exception.ResourceNotFoundException;
import com.amalitech.kanbantaskmanagement.security.CustomUserDetails;
import com.amalitech.kanbantaskmanagement.service.BoardService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final PagedResourcesAssembler<BoardSummaryDto> pagedResourcesAssembler;

    /**
     * Creates a new board for the specified user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BoardDetailDto>> createBoard(
            @Valid @RequestBody BoardRequestDTO requestDto) {

        BoardDetailDto createdBoard = boardService.createBoard(requestDto);

        ApiResponse<BoardDetailDto> response = ApiResponse.success(
                createdBoard,
                "Board created successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<EntityModel<BoardSummaryDto>>>> getAllBoards(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UUID userId = userDetails.getUserId();

            Page<BoardSummaryDto> boardsPage = boardService.getAccessibleBoards(userId, pageable);

            WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(BoardController.class).getAllBoards(authentication, pageable)
            );

            PagedModel<EntityModel<BoardSummaryDto>> pagedModel = pagedResourcesAssembler.toModel(
                    boardsPage,
                    linkBuilder.withSelfRel()
            );

            return ResponseEntity.ok(ApiResponse.success(pagedModel, "Boards fetched successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Retrieves a single board by its ID, along with its columns and tasks.
     * Access control: Only Board Owner or authorized Collaborator (Editor/Viewer) can view.
     */
    @GetMapping("/{boardId}")
    @PreAuthorize("isAuthenticated() and @boardPermissionValidator.canViewBoard(#boardId, authentication)")
    public ResponseEntity<ApiResponse<BoardDetailDto>> getBoardDetails(
            @PathVariable UUID boardId,
            Authentication authentication) {
        try {
            BoardDetailDto boardDetail = boardService.getBoardDetails(boardId);
            return ResponseEntity.ok(ApiResponse.success(boardDetail, "Board details fetched successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Board not found with ID: " + boardId));
        }
    }

    /**
     * Updates an existing board's details (e.g., name).
     * Access control: Only Board Owner or Editor collaborator can update.
     */
    @PutMapping("/{boardId}")
    @PreAuthorize("isAuthenticated() and @boardPermissionValidator.canEditBoard(#boardId, authentication)")
    public ResponseEntity<ApiResponse<BoardSummaryDto>> updateBoard(
            @PathVariable UUID boardId,
            @Valid @RequestBody BoardRequestDTO requestDto) {
        try {
            BoardSummaryDto updatedBoard = boardService.updateBoard(boardId, requestDto);
            return ResponseEntity.ok(ApiResponse.success(updatedBoard, "Board updated successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Board not found with ID: " + boardId));
        }
    }

    /**
     * Partially updates an existing board's details (e.g., only the name) - PARTIAL UPDATE (PATCH).
     * This typically applies incremental changes.
     * Access control: Only Board Owner or Editor collaborator can update.
     */
    @PatchMapping("/{boardId}")
    @PreAuthorize("isAuthenticated() and @boardPermissionValidator.canEditBoard(#boardId, authentication)")
    public ResponseEntity<ApiResponse<BoardSummaryDto>> patchBoard(
            @PathVariable UUID boardId,
            @Valid @RequestBody BoardPartialUpdateDto patchDto) {
        try {
            BoardSummaryDto updatedBoard = boardService.patchBoard(boardId, patchDto);
            return ResponseEntity.ok(ApiResponse.success(updatedBoard, "Board partially updated successfully"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Board not found with ID: " + boardId));
        }
    }


    /**
     * Deletes a board and all its associated data.
     * Access control: Only Board Owner can delete.
     */
    @DeleteMapping("/{boardId}")
    @PreAuthorize("isAuthenticated() and @boardPermissionValidator.canDeleteBoard(#boardId, authentication)")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable UUID boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Board deleted successfully"));
    }
}
