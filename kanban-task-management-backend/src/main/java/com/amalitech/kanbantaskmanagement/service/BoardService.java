package com.amalitech.kanbantaskmanagement.service;

import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardPartialUpdateDto;
import com.amalitech.kanbantaskmanagement.dto.request.Board.BoardRequestDTO;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardDetailDto;
import com.amalitech.kanbantaskmanagement.dto.response.board.BoardSummaryDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface BoardService {
    Page<BoardSummaryDto> getAllBoards(Pageable pageable);
    Page<BoardSummaryDto> getAccessibleBoards(UUID userId, Pageable pageable);
    BoardDetailDto createBoard(BoardRequestDTO requestDTO);
    BoardDetailDto getBoardDetails(UUID boardId);
    BoardSummaryDto updateBoard(UUID boardId, BoardRequestDTO requestDto);
    BoardSummaryDto patchBoard(UUID boardId, BoardPartialUpdateDto patchDto);
    void deleteBoard(UUID boardId);
}
