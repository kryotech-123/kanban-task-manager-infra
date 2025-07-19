package com.amalitech.kanbantaskmanagement.dto.response.board;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record BoardResponseDTO(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        String name,

        @JsonProperty("owner")
        BoardOwnerDto owner,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("updated_at")
        LocalDateTime updatedAt,

        @JsonProperty("columns_count")
        int columnsCount,

        @JsonProperty("collaborators_count")
        int collaboratorsCount
) {
    public record BoardOwnerDto(
            @JsonProperty("id")
            UUID id,

            @JsonProperty("name")
            String name,

            @JsonProperty("email")
            String email
    ) {
    }
}