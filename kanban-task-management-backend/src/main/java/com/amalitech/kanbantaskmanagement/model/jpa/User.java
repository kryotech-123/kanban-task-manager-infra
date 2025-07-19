package com.amalitech.kanbantaskmanagement.model.jpa;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"ownedBoards", "collaboratingBoards", "userProfile"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private UserProfile userProfile;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Board> ownedBoards = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<BoardCollaborator> collaboratingBoards = new HashSet<>();

    public void addOwnedBoard(Board board) {
        this.ownedBoards.add(board);
        board.setOwner(this);
    }

    public void removeOwnedBoard(Board board) {
        this.ownedBoards.remove(board);
        board.setOwner(null);
    }

    public void addCollaboratingBoard(BoardCollaborator collaborator) {
        this.collaboratingBoards.add(collaborator);
        collaborator.setUser(this);
    }

    public void removeCollaboratingBoard(BoardCollaborator collaborator) {
        this.collaboratingBoards.remove(collaborator);
        collaborator.setUser(null);
    }

    public void setUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            if (this.userProfile != null) {
                this.userProfile.setUser(null);
            }
        } else {
            userProfile.setUser(this);
        }
        this.userProfile = userProfile;
    }
}