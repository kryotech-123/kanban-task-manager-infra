package com.amalitech.kanbantaskmanagement.dto.response;

public record LoginResponse(
        String sessionId,
        String email
) {
}
