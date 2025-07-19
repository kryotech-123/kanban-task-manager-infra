package com.amalitech.kanbantaskmanagement.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

/**
 * Custom implementation of Spring Security's UserDetails to include the UUID of the user.
 * This allows direct access to the user's ID from the security context.
 */
@Getter
public class CustomUserDetails extends User {
    private final UUID userId;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
