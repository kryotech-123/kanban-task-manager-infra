package com.amalitech.kanbantaskmanagement.model.mongodb;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Document class representing an audit log entry in the project tracking system.
 *
 * An audit log records actions performed on entities within the system, such as
 * creation, updates, and deletions. Each log entry includes information about the
 * action type, the entity affected, when the action occurred, who performed it,
 * and additional payload data related to the action.
 *
 * This class is stored in MongoDB rather than a relational database to allow for
 * flexible schema and efficient storage of high-volume audit data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private ObjectId id;

    private ActionType actionType;
    private String entityType;
    private String entityId;
    private Instant timestamp = Instant.now();
    private String actorName;
    private String payload;

    private String ipAddress;
    private String userAgent;
    private String endpoint;

    /**
     * Enumeration of possible audit action types.
     * These types represent the different kinds of actions that can be audited.
     */
    public enum ActionType {
        CREATE,
        UPDATE,
        DELETE,
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        ACCESS_DENIED,
        REGISTRATION_SUCCESS,
        REGISTRATION_FAILURE,
        INVALID_TOKEN
    }
}