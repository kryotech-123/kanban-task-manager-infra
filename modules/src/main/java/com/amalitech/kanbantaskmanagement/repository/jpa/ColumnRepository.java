package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.Columns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColumnRepository extends JpaRepository<Columns, UUID> {
    List<Columns> findAllByBoardId(UUID boardId);
    List<Columns> findAllByBoardIdOrderByPositionAsc(UUID boardId);
    @Query("SELECT COALESCE(MAX(c.position), 0.0) FROM Columns c WHERE c.board.id = :boardId")
    Optional<Double> findMaxPositionByBoardId(@Param("boardId") UUID boardId);
}
