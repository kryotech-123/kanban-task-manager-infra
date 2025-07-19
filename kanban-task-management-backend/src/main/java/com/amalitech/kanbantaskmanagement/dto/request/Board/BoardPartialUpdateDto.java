package com.amalitech.kanbantaskmanagement.dto.request.Board;

import jakarta.validation.constraints.Size;
import java.util.Optional;

public record BoardPartialUpdateDto(
        @Size(min = 1, max = 100, message = "Board name must be between 1 and 100 characters")
        Optional<String> name
) {
}