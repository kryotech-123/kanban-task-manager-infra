package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardPartialUpdateDto;
import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardRequestDTO;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardDetailDto;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardSummaryDto;
import com.amalitech.kanbantaskmanagement.exception.ResourceNotFoundException;
import com.amalitech.kanbantaskmanagement.mapper.BoardMapper;
import com.amalitech.kanbantaskmanagement.model.jpa.Board;
import com.amalitech.kanbantaskmanagement.model.jpa.BoardCollaborator;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.model.jpa.enums.BoardPermission;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardCollaboratorRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import com.amalitech.kanbantaskmanagement.security.CustomUserDetails;
import com.amalitech.kanbantaskmanagement.service.BoardService;
import com.amalitech.kanbantaskmanagement.service.ColumnService;
import com.amalitech.kanbantaskmanagement.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BoardServiceImpl implements BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardMapper boardMapper;
    private final BoardCollaboratorRepository boardCollaboratorRepository;
    private final ColumnService columnService;
    private final EntityManager entityManager;

    public BoardServiceImpl(BoardRepository boardRepository,
                            UserRepository userRepository,
                            BoardMapper boardMapper,
                            BoardCollaboratorRepository boardCollaboratorRepository,
                            ColumnService columnService,
                            EntityManager entityManager) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardMapper = boardMapper;
        this.boardCollaboratorRepository = boardCollaboratorRepository;
        this.columnService = columnService;
        this.entityManager = entityManager;
    }

    @Override
    public Page<BoardSummaryDto> getAllBoards(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(boardMapper::toBoardSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardSummaryDto> getAccessibleBoards(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        return boardRepository.findAccessibleBoardsByUserId(userId, pageable)
                .map(boardMapper::toBoardSummaryDto);
    }

    @Override
    @Transactional
    public BoardDetailDto createBoard(BoardRequestDTO requestDto) {
        UUID ownerId = SecurityUtils.getCurrentAuthenticatedUserId();

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId.toString()));

        Board board = boardMapper.toBoardEntity(requestDto);
        board.setOwner(owner);
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);

        BoardCollaborator ownerCollaborator = BoardCollaborator.builder()
                .board(savedBoard)
                .user(owner)
                .permission(BoardPermission.ADMIN)
                .build();
        boardCollaboratorRepository.save(ownerCollaborator);

        if (requestDto.columns() != null && !requestDto.columns().isEmpty()) {
            List<String> orderedColumnNames = new ArrayList<>(requestDto.columns());
            columnService.createColumnsInBatch(orderedColumnNames, savedBoard.getId());
        }

        entityManager.refresh(savedBoard);

        Board boardWithColumnsAndTasks = boardRepository.findBoardWithColumnsAndTasksById(savedBoard.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", savedBoard.getId().toString()));

        return boardMapper.toBoardDto(boardWithColumnsAndTasks);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardDetailDto getBoardDetails(UUID boardId) {
        Board board = boardRepository.findBoardWithColumnsAndTasksById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId.toString()));

        return boardMapper.toBoardDto(board);
    }

    @Override
    @Transactional
    public BoardSummaryDto updateBoard(UUID boardId, BoardRequestDTO requestDto) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId.toString()));

        if (!existingBoard.getName().equals(requestDto.name())) {
            if (boardRepository.findByNameAndOwnerId(requestDto.name(), existingBoard.getOwner().getId()).isPresent()) {
                throw new IllegalArgumentException(
                        "Board with name '" + requestDto.name() + "' already exists for this user."
                );
            }
        }

        existingBoard.setName(requestDto.name());
        existingBoard.setUpdatedAt(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(existingBoard);
        return boardMapper.toBoardSummaryDto(updatedBoard);
    }

    @Override
    @Transactional
    public BoardSummaryDto patchBoard(UUID boardId, BoardPartialUpdateDto patchDto) {
        Board existingBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board", "id", boardId.toString()));

        patchDto.name().ifPresent(newName -> {
            if (newName.trim().isEmpty()) {
                throw new IllegalArgumentException("Board name cannot be empty.");
            }

            if (!existingBoard.getName().equals(newName)) {
                if (boardRepository.findByNameAndOwnerId(newName, existingBoard.getOwner().getId()).isPresent()) {
                    throw new IllegalArgumentException(
                            "Board with name '" + newName + "' already exists for this user."
                    );
                }
            }
            existingBoard.setName(newName);
        });

        existingBoard.setUpdatedAt(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(existingBoard);

        return boardMapper.toBoardSummaryDto(updatedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(UUID boardId) {
        boardRepository.findById(boardId).ifPresent(boardRepository::delete);
    }

    private static UUID getUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Authentication principal is not of type CustomUserDetails");
        }

        return userDetails.getUserId();
    }

}