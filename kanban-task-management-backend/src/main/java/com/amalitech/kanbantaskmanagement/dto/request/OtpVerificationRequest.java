package com.amalitech.kanbantaskmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record OtpVerificationRequest(
        @NotNull(message = "Session ID cannot be empty")
        UUID sessionId,

        @NotBlank(message = "OTP code cannot be empty")
        @Pattern(regexp = "\\d{4}", message = "OTP must be a 4-digit number")
        String otp
) {
}