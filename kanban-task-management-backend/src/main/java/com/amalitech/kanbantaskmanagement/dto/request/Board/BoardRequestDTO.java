package com.amalitech.kanbantaskmanagement.dto.request.Board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import java.util.Collections;

public record BoardRequestDTO(
        @NotBlank(message = "Board name is required")
        @Size(min = 1, max = 254, message = "Board name must be between 1 and 254 characters")
        String name,

        Set<String> columns
) {
        public BoardRequestDTO(String name, Set<String> columns) {
                this.name = name;
                this.columns = columns == null ?
                        Collections.emptySet() :
                        Collections.unmodifiableSet(columns);
        }
}
