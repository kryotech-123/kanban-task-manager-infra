package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


/**
 * Repository interface for performing CRUD operations and custom queries
 * on {@link User} entities in the database.
 * <p>
 * This interface extends {@link JpaRepository}, which provides standard
 * methods like save, findById, findAll, and deleteById out of the box.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
}
