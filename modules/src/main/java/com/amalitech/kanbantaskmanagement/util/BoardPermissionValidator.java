package com.amalitech.kanbantaskmanagement.util;

import com.amalitech.kanbantaskmanagement.model.jpa.BoardCollaborator;
import com.amalitech.kanbantaskmanagement.model.jpa.enums.BoardPermission;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardRepository;
import com.amalitech.kanbantaskmanagement.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("boardPermissionValidator")
@RequiredArgsConstructor
public class BoardPermissionValidator {
    private final BoardRepository boardRepository;

    public boolean canViewBoard(UUID boardId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        BoardPermission permission = getUserPermissionOnBoard(boardId, userId);

        return (
                permission == BoardPermission.ADMIN ||
                        permission == BoardPermission.EDITOR ||
                        permission == BoardPermission.VIEWER
        );
    }

    public boolean canEditBoard(UUID boardId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        BoardPermission permission = getUserPermissionOnBoard(boardId, userId);

        return (
                permission == BoardPermission.ADMIN ||
                        permission == BoardPermission.EDITOR
        );
    }

    public boolean canDeleteBoard(UUID boardId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UUID userId = userDetails.getUserId();

        return boardRepository.findById(boardId)
                .map(board -> {
                    if (board.getOwner().getId().equals(userId)) {
                        return true;
                    }

                    return false;
                })
                .orElse(false);
    }

     public boolean canManageCollaborators(UUID boardId, Authentication authentication) {
         CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
         UUID userId = userDetails.getUserId();

         BoardPermission permission = getUserPermissionOnBoard(boardId, userId);

         return permission == BoardPermission.ADMIN;
     }

    private BoardPermission getUserPermissionOnBoard(UUID boardId, UUID userId) {
        return boardRepository.findById(boardId)
                .flatMap(board -> {
                    if (board.getOwner().getId().equals(userId)) {
                        return Optional.of(BoardPermission.ADMIN);
                    }

                    return board.getBoardCollaborators().stream()
                            .filter(bc -> bc.getUser().getId().equals(userId))
                            .map(BoardCollaborator::getPermission)
                            .findFirst();
                })
                .orElse(null);
    }
}