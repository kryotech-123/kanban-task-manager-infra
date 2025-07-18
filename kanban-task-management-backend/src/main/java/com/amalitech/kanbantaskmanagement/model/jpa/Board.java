package com.amalitech.kanbantaskmanagement.model.jpa;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "boards", indexes = {
        @Index(name = "idx_board_owner_id", columnList = "owner_id")
})
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Board.withOwnerAndCollaborators",
                attributeNodes = {
                        @NamedAttributeNode("owner"),
                        @NamedAttributeNode("boardCollaborators")
                }
        ),
        @NamedEntityGraph(
                name = "Board.full",
                attributeNodes = {
                        @NamedAttributeNode("owner"),
                        @NamedAttributeNode("columns"),
                        @NamedAttributeNode("boardCollaborators")
                }
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"columns", "boardCollaborators", "owner"})
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private Set<Columns> columns = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BoardCollaborator> boardCollaborators = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = this.createdAt;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addColumn(Columns column) {
        this.columns.add(column);
        column.setBoard(this);
    }

    public void removeColumn(Columns column) {
        this.columns.remove(column);
        column.setBoard(null);
    }

    public void addCollaborator(BoardCollaborator collaborator) {
        this.boardCollaborators.add(collaborator);
        collaborator.setBoard(this);
    }

    public void removeCollaborator(BoardCollaborator collaborator) {
        this.boardCollaborators.remove(collaborator);
        collaborator.setBoard(null);
    }
}
