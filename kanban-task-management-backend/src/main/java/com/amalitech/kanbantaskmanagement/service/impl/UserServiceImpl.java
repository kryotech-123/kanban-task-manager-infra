package com.amalitech.kanbantaskmanagement.service.impl;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserRegistrationRequest;
import com.amalitech.kanbantaskmanagement.exception.EmailAlreadyExistException;
import com.amalitech.kanbantaskmanagement.exception.UsernameAlreadyExistException;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.model.jpa.UserProfile;
import com.amalitech.kanbantaskmanagement.model.jpa.enums.UserTheme;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserProfileRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;

import com.amalitech.kanbantaskmanagement.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public User registerUser(UserRegistrationRequest dto) {
        validateUniqueEmailAndUsername(dto);

        User user = buildUserFromRequest(dto);
        user = userRepository.save(user);

        UserProfile profile = buildDefaultUserProfile(user);
        userProfileRepository.save(profile);

        return user;
    }

    private void validateUniqueEmailAndUsername(UserRegistrationRequest dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistException("Email is already taken");
        }

        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameAlreadyExistException("Username is already taken");
        }
    }

    private User buildUserFromRequest(UserRegistrationRequest dto) {
        String hashedPassword = passwordEncoder.encode(dto.password());

        return User.builder()
                .firstName(sanitizeNullableField(dto.firstname()))
                .middleName(sanitizeNullableField(dto.middlename()))
                .lastName(sanitizeNullableField(dto.lastname()))
                .username(dto.username())
                .email(dto.email())
                .password(hashedPassword)
                .build();
    }


    private UserProfile buildDefaultUserProfile(User user) {
        return UserProfile.builder()
                .user(user)
                .theme(UserTheme.LIGHT)
                .avatar(null)
                .build();
    }

    private String sanitizeNullableField(String input) {
        return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
    }


}