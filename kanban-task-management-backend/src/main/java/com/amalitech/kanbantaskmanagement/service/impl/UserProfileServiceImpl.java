package com.amalitech.kanbantaskmanagement.service.impl;


import com.amalitech.kanbantaskmanagement.dto.request.user.UserProfileUpdateRequest;
import com.amalitech.kanbantaskmanagement.dto.response.user.UserProfileResponse;
import com.amalitech.kanbantaskmanagement.exception.EmailAlreadyExistException;
import com.amalitech.kanbantaskmanagement.exception.InvalidPasswordException;
import com.amalitech.kanbantaskmanagement.exception.ProfileNotFoundException;
import com.amalitech.kanbantaskmanagement.exception.UserNotFoundException;
import com.amalitech.kanbantaskmanagement.mapper.UserProfileMapper;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.model.jpa.UserProfile;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserProfileRepository;
import com.amalitech.kanbantaskmanagement.repository.jpa.UserRepository;
import com.amalitech.kanbantaskmanagement.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Primary
public class UserProfileServiceImpl implements UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public  UserProfileResponse getProfile() {
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));

        return UserProfileMapper.toResponse(user, profile);
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else {
            return principal.toString();
        }
    }

    @Transactional
    public UserProfileResponse updateProfile(UserProfileUpdateRequest request) {
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));



        Optional.ofNullable(request.newEmail())
                .filter(newEmail -> !newEmail.equals(user.getEmail()))
                .ifPresent(newEmail -> {
                    if (userRepository.existsByEmail(newEmail)) {
                        throw new EmailAlreadyExistException("Email is already in use.");
                    }
                    user.setEmail(newEmail);
                });

        if (request.currentPassword().isPresent() && request.newPassword().isPresent()) {
            String currentPassword = request.currentPassword().get();
            String newPassword = request.newPassword().get();

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
        }


        request.firstName().ifPresent(firstName -> {
            if (!firstName.equals(user.getFirstName())) {
                user.setFirstName(firstName);
            }
        });

        request.lastName().ifPresent(lastName -> {
            if (!lastName.equals(user.getLastName())) {
                user.setLastName(lastName);
            }
        });

        request.theme().ifPresent(profile::setTheme);

        request.avatar().ifPresent(profile::setAvatar);


        User savedUser = userRepository.save(user);
        UserProfile savedProfile = userProfileRepository.save(profile);


        return UserProfileMapper.toResponse(savedUser, savedProfile);
    }

}
