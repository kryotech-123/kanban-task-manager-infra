package com.amalitech.kanbantaskmanagement.dto.response;


public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}