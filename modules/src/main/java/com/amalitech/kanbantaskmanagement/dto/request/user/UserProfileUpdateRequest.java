package com.amalitech.kanbantaskmanagement.dto.request.user;

import com.amalitech.kanbantaskmanagement.model.jpa.enums.UserTheme;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;


public record UserProfileUpdateRequest(
        @Email
        String newEmail,

        Optional<String> avatar,

        Optional<UserTheme> theme,

        Optional<String> firstName,

        Optional<String> lastName,

        Optional<@Size(min = 8, max = 64) String> currentPassword,

        Optional<@Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&\\.]{8,64}$",
                message = "New password must include uppercase, number, and special character"
        ) String> newPassword
) {
}