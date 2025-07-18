package com.amalitech.kanbantaskmanagement.dto.request.column;

import jakarta.validation.constraints.NotBlank;

public record ColumnRequestDto(
        @NotBlank(message = "Column name must not be blank") String name
) {}
