package com.amalitech.kanbantaskmanagement.mapper;


import com.amalitech.kanbantaskmanagement.dto.response.user.UserProfileResponse;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.model.jpa.UserProfile;
import lombok.Builder;


public class UserProfileMapper {

    public static UserProfileResponse toResponse(User user, UserProfile profile) {
        return UserProfileResponse.builder()
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatar(profile.getAvatar())
                .theme(profile.getTheme())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
