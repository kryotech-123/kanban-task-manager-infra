package com.amalitech.kanbantaskmanagement.util;

import com.amalitech.kanbantaskmanagement.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for common security-related operations,
 * particularly for extracting current user details.
 */
@Component
public class SecurityUtils {

    /**
     * Retrieves the current Authentication object from the SecurityContext.
     *
     * @return The Authentication object.
     * @throws IllegalStateException if no authentication is present in the SecurityContext.
     */
    public static Authentication getCurrentAuthenticatedAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated. No Authentication object found.");
        }
        return authentication;
    }

    /**
     * Retrieves the UUID of the currently authenticated user.
     * Throws IllegalStateException if the user is not authenticated or
     * the principal is not of type CustomUserDetails.
     *
     * @return The UUID of the authenticated user.
     * @throws IllegalStateException if authentication fails or user details are not available.
     */
    public static UUID getCurrentAuthenticatedUserId() {
        Authentication authentication = getCurrentAuthenticatedAuthentication();

        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated. Cannot retrieve user ID.");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("Authentication principal is not of type CustomUserDetails. " +
                    "Check your Spring Security configuration or principal object.");
        }

        return userDetails.getUserId();
    }
}
