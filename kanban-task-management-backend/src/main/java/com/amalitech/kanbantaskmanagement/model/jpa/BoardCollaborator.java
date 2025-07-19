package com.amalitech.kanbantaskmanagement.model.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.enums.BoardPermission;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "board_collaborators", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"board_id", "user_id"}, name = "uc_board_user_collaboration")
}, indexes = {
        @Index(name = "idx_board_collaborator_board_id", columnList = "board_id"),
        @Index(name = "idx_board_collaborator_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"board", "user"})
public class BoardCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false)
    private BoardPermission permission;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}