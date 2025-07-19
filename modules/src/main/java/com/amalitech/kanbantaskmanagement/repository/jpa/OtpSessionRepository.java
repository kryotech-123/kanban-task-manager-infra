package com.amalitech.kanbantaskmanagement.repository.jpa;

import com.amalitech.kanbantaskmanagement.model.jpa.OtpSession;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpSessionRepository extends JpaRepository<OtpSession, UUID> {
    /**
     * Finds an OtpSession by its session ID and eagerly fetches the associated User entity.
     * This prevents the LazyInitializationException that can occur when accessing the user
     * from a different transaction than the one that created the session.
     *
     * @param sessionId The UUID of the session to find.
     * @return An Optional containing the OtpSession with the user pre-loaded.
     */
    @Query("SELECT os FROM OtpSession os JOIN FETCH os.user WHERE os.sessionId = :sessionId")
    Optional<OtpSession> findBySessionIdWithUser(@Param("sessionId") UUID sessionId);

    Optional<OtpSession> findBySessionId(UUID sessionId);
}
