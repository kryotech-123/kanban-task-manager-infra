package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.Board;
import com.amalitech.kanbantaskmanagement.model.jpa.BoardCollaborator;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardCollaboratorRepository extends JpaRepository<BoardCollaborator, Long> {
    List<BoardCollaborator> findByUser(User user);
    Optional<BoardCollaborator> findByBoardAndUser(Board board, User user);
}
