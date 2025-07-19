package com.amalitech.kanbantaskmanagement.repository.mongodb;

import com.amalitech.kanbantaskmanagement.model.mongodb.AuditLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    Page<AuditLog> findByActorName(String actorName, Pageable pageable);

    @Query("{'timestamp': {$gte: ?0, $lte: ?1}}")
    Page<AuditLog> findByTimestampBetween(Instant startDate, Instant endDate, Pageable pageable);

    Page<AuditLog> findByActionType(AuditLog.ActionType actionType, Pageable pageable);
}
