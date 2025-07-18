package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.model.jpa.BoardCollaborator;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.repository.jpa.BoardCollaboratorRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import com.amalitech.kanbantaskmanagement.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final BoardCollaboratorRepository boardCollaboratorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.amalitech.kanbantaskmanagement.model.jpa.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<BoardCollaborator> collaborations = boardCollaboratorRepository.findByUser(user);

        Set<SimpleGrantedAuthority> authorities = collaborations.stream()
                .map(collaborator -> {
                    return new SimpleGrantedAuthority("ROLE_" + collaborator.getPermission().name());
                }).collect(Collectors.toSet());


        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getId()
        );
    }

    /**
     * Helper method to fetch the JPA User entity directly from the repository.
     * This is useful for filters or services that need the full entity,
     * e.g., for token validation in JwtService.isTokenValid.
     */
    public User getUserByUsername(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + usernameOrEmail));
    }
}