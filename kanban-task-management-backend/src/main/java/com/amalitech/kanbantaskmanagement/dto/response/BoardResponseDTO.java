package com.amalitech.kanbantaskmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BoardResponseDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private BoardOwnerDto owner;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("columns_count")
    private int columnsCount;

    @JsonProperty("collaborators_count")
    private int collaboratorsCount;

    @Data
    @Builder
    public static class BoardOwnerDto {
        @JsonProperty("id")
        private UUID id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;
    }
}
