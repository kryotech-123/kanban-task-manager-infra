package com.amalitech.kanbantaskmanagement.dto.request.Board;

import com.amalitech.kanbantaskmanagement.model.jpa.Columns;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public record BoardUpdateDto(
        UUID id,
        Optional<String> name,
        Optional<HashSet<Columns>> columns
) {
}
