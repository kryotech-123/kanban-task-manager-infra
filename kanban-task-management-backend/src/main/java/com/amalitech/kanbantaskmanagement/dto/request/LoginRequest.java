package com.amalitech.kanbantaskmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Username or email cannot be empty")
        String usernameOrEmail,

        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
