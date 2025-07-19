package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.Board;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    @EntityGraph(attributePaths = {"columns", "columns.tasks"})
    Optional<Board> findById(UUID id);

    @EntityGraph(value = "Board.withOwner", type = EntityGraph.EntityGraphType.LOAD)
    Page<Board> findAll(@NotNull Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN FETCH b.columns c " +
            "LEFT JOIN FETCH c.tasks t " +
            "WHERE b.id = :boardId")
    Optional<Board> findBoardWithColumnsAndTasksById(@Param("boardId") UUID boardId);


    @EntityGraph(value = "Board.withOwnerAndCollaborators", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT DISTINCT b FROM Board b " +
            "LEFT JOIN b.boardCollaborators bc " +
            "WHERE b.owner.id = :userId OR bc.user.id = :userId")
    Page<Board> findAccessibleBoardsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT b FROM Board b " +
            "LEFT JOIN FETCH b.columns c " +
            "LEFT JOIN FETCH b.boardCollaborators bc " +
            "WHERE b.id = :boardId")
    Optional<Board> findBoardWithColumnsAndCollaboratorsById(@Param("boardId") UUID boardId);

    Optional<Board> findByNameAndOwnerId(String name, UUID ownerId);
}
