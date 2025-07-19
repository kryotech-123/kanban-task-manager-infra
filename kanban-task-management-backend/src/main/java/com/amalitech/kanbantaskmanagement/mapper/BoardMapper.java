package com.amalitech.kanbantaskmanagement.mapper;

import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardRequestDTO;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardDetailDto;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardSummaryDto;
import com.amalitech.kanbantaskmanagement.dto.response.collaborator.BoardCollaboratorDto;
import com.amalitech.kanbantaskmanagement.dto.response.column.ColumnDto;
import com.amalitech.kanbantaskmanagement.model.jpa.Board;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BoardMapper {

    private final ColumnMapper columnMapper;
    private final BoardCollaboratorMapper boardCollaboratorMapper;
    private final UserRepository userRepository;

    public BoardMapper(ColumnMapper columnMapper,
                       BoardCollaboratorMapper boardCollaboratorMapper,
                       UserRepository userRepository) {
        this.columnMapper = columnMapper;
        this.boardCollaboratorMapper = boardCollaboratorMapper;
        this.userRepository = userRepository;
    }

    public BoardDetailDto toBoardDto(Board board) {
        String ownerUsername = (board.getOwner() != null) ? board.getOwner().getUsername() : null;

        List<ColumnDto> columnDtos = board.getColumns().stream()
                .map(columnMapper::toColumnDto)
                .sorted(Comparator.comparing(ColumnDto::position))
                .collect(Collectors.toList());

        List<BoardCollaboratorDto> collaboratorDtos = board.getBoardCollaborators().stream()
                .map(boardCollaboratorMapper::toBoardCollaboratorDto)
                .collect(Collectors.toList());

        return new BoardDetailDto(
                board.getId(),
                board.getName(),
                board.getOwner().getId(),
                ownerUsername,
                board.getCreatedAt(),
                board.getUpdatedAt(),
                columnDtos,
                collaboratorDtos
        );
    }

    public BoardSummaryDto toBoardSummaryDto(Board board) {
        if (board == null) {
            return null;
        }

        String ownerUsername = (board.getOwner() != null) ? board.getOwner().getUsername() : null;

        return new BoardSummaryDto(
                board.getId(),
                board.getName(),
                board.getOwner().getId(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }

    public Board toBoardEntity(BoardRequestDTO boardDto) {
        Board boardEntity = new Board();
        boardEntity.setName(boardDto.name());

        return boardEntity;
    }

    public void updateBoardEntity(BoardDetailDto boardDto, Board existingBoard) {
        existingBoard.setName(boardDto.name());
    }
}
