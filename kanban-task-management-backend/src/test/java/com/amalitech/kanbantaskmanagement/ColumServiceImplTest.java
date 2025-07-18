package com.amalitech.kanbantaskmanagement;

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
import com.amalitech.kanbantaskmanagement.service.impl.ColumnServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnServiceImplTest {

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ColumnMapper columnMapper;

    @InjectMocks
    private ColumnServiceImpl columnService;

    private UUID boardId;
    private UUID columnId;
    private Board board;
    private Columns column;
    private ColumnDto columnDto;
    private ColumnRequestDto columnRequestDto;

    @BeforeEach
    void setup() {
        boardId = UUID.randomUUID();
        columnId = UUID.randomUUID();

        board = Board.builder().id(boardId).build();

        column = Columns.builder()
                .id(columnId)
                .name("Todo")
                .board(board)
                .position(1.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        columnDto = new ColumnDto(
                columnId,
                "Todo",
                1.0,
                column.getCreatedAt(),
                column.getUpdatedAt(),
                List.of()
        );

        columnRequestDto = new ColumnRequestDto("Todo");
    }

    @Test
    void testCreateColumn_SetOfNames_Success() {
        // Given
        List<String> columnNames = List.of("Todo", "In Progress");
        UUID boardId = UUID.randomUUID();

        // Create test board
        Board board = new Board();
        board.setId(boardId);

        // Create test columns
        Columns todoColumn = Columns.builder()
                .name("TODO")
                .position(2.0)
                .board(board)
                .build();

        Columns inProgressColumn = Columns.builder()
                .name("IN PROGRESS")
                .position(3.0)
                .board(board)
                .build();

        // Mock repository responses
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(columnRepository.findMaxPositionByBoardId(boardId)).thenReturn(Optional.of(1.0));

        // Mock saveAll - return the input list with IDs set
        when(columnRepository.saveAll(anyList())).thenAnswer(invocation -> {
            List<Columns> input = invocation.getArgument(0);
            input.get(0).setId(UUID.randomUUID());
            input.get(1).setId(UUID.randomUUID());
            return input;
        });

        // Mock findAll to return both columns
        when(columnRepository.findAllByBoardId(boardId)).thenReturn(List.of(todoColumn, inProgressColumn));

        // Mock mapper
        when(columnMapper.toColumnDto(any(Columns.class))).thenAnswer(invocation -> {
            Columns column = invocation.getArgument(0);
            return new ColumnDto(
                    column.getId(),
                    column.getName(),
                    column.getPosition(),
                    column.getCreatedAt(),
                    column.getUpdatedAt(),
                    List.of()
            );
        });

        // When
        List<ColumnDto> result = columnService.createColumnsInBatch(columnNames, boardId);

        // Then
        assertEquals(2, result.size());

        // Verify saveAll was called with a list of 2 columns
        verify(columnRepository).saveAll(argThat((List<Columns> list) ->
                list.size() == 2 &&
                        list.get(0).getName().equals("TODO") &&
                        list.get(1).getName().equals("IN PROGRESS")
        ));

        // Verify positions
        verify(columnRepository).saveAll(argThat((List<Columns> list) ->
                list.getFirst().getPosition() == 2.0 &&
                        list.get(1).getPosition() == 3.0
        ));
    }


    @Test
    void testCreateColumn_SetOfNames_BoardNotFound() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThrows(BoardNotFoundException.class, () -> columnService.createColumnsInBatch(List.of("Todo"), boardId));
    }

    @Test
    void testCreateColumn_SingleDto_Success() {
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        when(columnRepository.findMaxPositionByBoardId(boardId)).thenReturn(Optional.of(2.0));
        when(columnRepository.save(any())).thenReturn(column);
        when(columnMapper.toColumnDto(column)).thenReturn(columnDto);

        ColumnDto result = columnService.createSingleColumn(boardId, columnRequestDto);

        assertEquals("Todo", result.name());
        verify(columnRepository).save(any());
    }

    @Test
    void testUpdateColumn_Success() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnRepository.save(any())).thenReturn(column);
        when(columnMapper.toColumnDto(column)).thenReturn(columnDto);

        ColumnDto result = columnService.updateColumn(columnId, columnRequestDto);

        assertEquals("Todo", result.name());
        verify(columnRepository).save(column);
    }

    @Test
    void testUpdateColumn_ColumnNotFound() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        assertThrows(ColumnNotFoundException.class, () -> columnService.updateColumn(columnId, columnRequestDto));
    }

    @Test
    void testDeleteColumn_Success() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));

        columnService.deleteColumn(columnId);

        verify(columnRepository).delete(column);
    }

    @Test
    void testDeleteColumn_NotFound() {
        UUID nonExistentColumnId = UUID.randomUUID();
        when(columnRepository.findById(nonExistentColumnId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> columnService.deleteColumn(nonExistentColumnId));

        verify(columnRepository).findById(nonExistentColumnId);
        verify(columnRepository, never()).delete(any());
    }

    @Test
    void testGetColumn_Success() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.of(column));
        when(columnMapper.toColumnDto(column)).thenReturn(columnDto);

        ColumnDto result = columnService.getColumn(columnId);

        assertEquals("Todo", result.name());
    }

    @Test
    void testGetColumn_NotFound() {
        when(columnRepository.findById(columnId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> columnService.getColumn(columnId));
    }

    @Test
    void testGetColumnsForBoard_Success() {
        when(columnRepository.findAllByBoardIdOrderByPositionAsc(boardId)).thenReturn(List.of(column));
        when(columnMapper.toColumnDto(column)).thenReturn(columnDto);

        List<ColumnDto> result = columnService.getColumnsForBoard(boardId);

        assertEquals(1, result.size());
        verify(columnRepository).findAllByBoardIdOrderByPositionAsc(boardId);
    }
}
