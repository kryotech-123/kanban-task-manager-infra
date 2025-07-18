package com.amalitech.kanbantaskmanagement.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        String firstname,

        String middlename,

        String lastname,

        @NotBlank
        String username,

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&\\.]{8,64}$",
                message = "Password must contain at least one uppercase letter, one number, and one special character."
        )
        String password
) {
}