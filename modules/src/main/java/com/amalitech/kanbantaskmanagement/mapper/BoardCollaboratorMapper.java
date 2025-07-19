package com.amalitech.kanbantaskmanagement.mapper;

import com.amalitech.kanbantaskmanagement.dto.response.collaborator.BoardCollaboratorDto;
import com.amalitech.kanbantaskmanagement.model.jpa.Board;
import com.amalitech.kanbantaskmanagement.model.jpa.BoardCollaborator;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Mapper for BoardCollaborator entity and its DTO.
 * Handles conversion between BoardCollaborator entity and BoardCollaboratorDto.
 */
@Component
public class BoardCollaboratorMapper {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public BoardCollaboratorMapper(UserRepository userRepository, BoardRepository boardRepository) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    /**
     * Converts a BoardCollaborator entity to a BoardCollaboratorDto.
     * This method expects the associated User entity to be initialized
     * (either eagerly fetched or accessed within a transactional context).
     *
     * @param collaborator The BoardCollaborator entity to convert.
     * @return The corresponding BoardCollaboratorDto.
     */
    public BoardCollaboratorDto toBoardCollaboratorDto(BoardCollaborator collaborator) {
        UUID userId = null;
        String username = null;
        if (collaborator.getUser() != null) {
            userId = collaborator.getUser().getId();
            username = collaborator.getUser().getUsername();
        }

        return new BoardCollaboratorDto(
                collaborator.getId(),
                userId,
                username,
                collaborator.getBoard().getId(),
                collaborator.getPermission(),
                collaborator.getCreatedAt(),
                collaborator.getUpdatedAt()
        );
    }

    /**
     * Converts a BoardCollaboratorDto to a BoardCollaborator entity for creation/update.
     * Note: The board and user relationships must be resolved by fetching from the database.
     *
     * @param collaboratorDto The BoardCollaboratorDto to convert.
     * @param existingCollaborator Optional existing BoardCollaborator entity for updates.
     * @return A new or updated BoardCollaborator entity.
     */
    public BoardCollaborator toBoardCollaboratorEntity(BoardCollaboratorDto collaboratorDto, BoardCollaborator existingCollaborator) {
        BoardCollaborator collaboratorEntity = Optional.ofNullable(existingCollaborator).orElseGet(BoardCollaborator::new);

        if (collaboratorDto.userId() != null) {
            User user = userRepository.findById(collaboratorDto.userId())
                    .orElseThrow(() -> new IllegalArgumentException("Collaborator user not found with ID: " + collaboratorDto.userId()));
            collaboratorEntity.setUser(user);
        } else {
            throw new IllegalArgumentException("Collaborator user ID cannot be null.");
        }

        if (collaboratorDto.boardId() != null) {
            Board board = boardRepository.findById(collaboratorDto.boardId())
                    .orElseThrow(() -> new IllegalArgumentException("Board not found with ID: " + collaboratorDto.boardId()));
            collaboratorEntity.setBoard(board);
        } else {
            throw new IllegalArgumentException("Board ID cannot be null for a collaborator.");
        }


        collaboratorEntity.setPermission(collaboratorDto.permission());

        return collaboratorEntity;
    }
}