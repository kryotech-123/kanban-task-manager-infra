package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.RefreshToken;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findAllByUserAndIsValid(User user, Boolean isValid);
}