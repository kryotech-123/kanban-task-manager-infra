package com.amalitech.kanbantaskmanagement;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserRegistrationRequest;
import com.amalitech.kanbantaskmanagement.exception.EmailAlreadyExistException;
import com.amalitech.kanbantaskmanagement.exception.UsernameAlreadyExistException;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserProfileRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import com.amalitech.kanbantaskmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private UserRegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new UserRegistrationRequest(
                "John",
                "Middle",
                "Doe",
                "johndoe",
                "john@example.com",
                "Password1!"
        );
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John",
                "Middle",
                "Doe",
                "johndoe",
                "john@example.com",
                "ValidPass123!"
        );

        // Mock dependencies
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");

        // Use ArgumentCaptor to verify the saved user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Return a new user with all fields preserved
            return User.builder()
                    .firstName(user.getFirstName())
                    .middleName(user.getMiddleName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();
        });

        // When
        User result = userServiceImpl.registerUser(request);

        // Then
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Middle", savedUser.getMiddleName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("johndoe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
    }

    @Test
    void registerUser_ShouldThrowEmailAlreadyExistException() {
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> userServiceImpl.registerUser(request));
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowUsernameAlreadyExistException() {
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(UsernameAlreadyExistException.class, () -> userServiceImpl.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldHandleNullMiddlename() {
        request = new UserRegistrationRequest(
                "John",
                "   ","Doe",
                "johndoe",
                "john@example.com",
                "Password1!"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userServiceImpl.registerUser(request);

        assertNull(result.getMiddleName());
    }

    @Test
    void registerUser_ShouldHandleBlankMiddlename() {
        request = new UserRegistrationRequest(
               "John",
                "   ","Doe",
                "johndoe",
                "john@example.com",
                "Password1!"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userServiceImpl.registerUser(request);

        assertNull(result.getMiddleName());
    }
}