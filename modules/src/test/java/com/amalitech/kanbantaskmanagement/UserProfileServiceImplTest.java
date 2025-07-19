package com.amalitech.kanbantaskmanagement;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserProfileUpdateRequest;
import com.amalitech.kanbantaskmanagement.dto.response.user.UserProfileResponse;
import com.amalitech.kanbantaskmanagement.exception.EmailAlreadyExistException;
import com.amalitech.kanbantaskmanagement.exception.InvalidPasswordException;
import com.amalitech.kanbantaskmanagement.exception.ProfileNotFoundException;
import com.amalitech.kanbantaskmanagement.exception.UserNotFoundException;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.model.jpa.UserProfile;
import com.amalitech.kanbantaskmanagement.model.jpa.enums.UserTheme;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserProfileRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;

import com.amalitech.kanbantaskmanagement.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserProfileServiceImpl userProfileService;

    private final UUID userId = UUID.randomUUID();
    private final String username = "johndoe";
    private final String email = "john@example.com";
    private final String password = "encodedPassword";

    private User user;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .username(username)
                .email(email)
                .firstName("John")
                .lastName("Doe")
                .password(password)
                .build();

        profile = UserProfile.builder()
                .id(UUID.randomUUID())
                .user(user)
                .avatar("old-avatar.png")
                .theme(UserTheme.DARK)
                .build();

        user.setUserProfile(profile);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username, "", List.of()
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void getProfile_success() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        UserProfileResponse response = userProfileService.getProfile();

        assertEquals(username, response.getUsername());
        assertEquals(email, response.getEmail());
        verify(userRepository).findByUsername(username);
        verify(userProfileRepository).findByUserId(userId);
    }

    @Test
    void getProfile_userNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userProfileService.getProfile());
    }

    @Test
    void getProfile_profileNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> userProfileService.getProfile());
    }

    @Test
    void updateProfile_successfulUpdate() {
        String newEmail = "new@example.com";
        String newAvatar = "new-avatar.png";
        String newFirst = "Jane";
        String newLast = "Smith";
        String currentPass = "currentPass";
        String newPass = "NewPass123!";

        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                newEmail,
                Optional.of(newAvatar),
                Optional.of(UserTheme.LIGHT),
                Optional.of(newFirst),
                Optional.of(newLast),
                Optional.of(currentPass),
                Optional.of(newPass)
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(passwordEncoder.matches(currentPass, password)).thenReturn(true);
        when(passwordEncoder.encode(newPass)).thenReturn("encodedNewPass");
        when(userRepository.save(any())).thenReturn(user);
        when(userProfileRepository.save(any())).thenReturn(profile);

        UserProfileResponse response = userProfileService.updateProfile(request);

        assertEquals(newEmail, response.getEmail());
        assertEquals(newFirst, response.getFirstName());
        assertEquals(newLast, response.getLastName());
        assertEquals(UserTheme.LIGHT, response.getTheme());
        assertEquals(newAvatar, response.getAvatar());
    }

    @Test
    void updateProfile_emailAlreadyExists() {
        String newEmail = "new@example.com";
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                newEmail,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userRepository.existsByEmail(newEmail)).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> userProfileService.updateProfile(request));
    }

    @Test
    void updateProfile_invalidCurrentPassword() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                email,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("wrongPassword"),
                Optional.of("NewPass123!")
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(passwordEncoder.matches("wrongPassword", password)).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userProfileService.updateProfile(request));
    }

    @Test
    void updateProfile_firstAndLastNameUpdated() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest(
                email,
                Optional.empty(),
                Optional.empty(),
                Optional.of("NewFirst"),
                Optional.of("NewLast"),
                Optional.empty(),
                Optional.empty()
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userRepository.save(any())).thenReturn(user);
        when(userProfileRepository.save(any())).thenReturn(profile);

        UserProfileResponse response = userProfileService.updateProfile(request);

        assertEquals("NewFirst", response.getFirstName());
        assertEquals("NewLast", response.getLastName());
    }
}
