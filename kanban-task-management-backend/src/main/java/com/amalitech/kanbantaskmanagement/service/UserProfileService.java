package com.amalitech.kanbantaskmanagement.service;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserProfileUpdateRequest;
import com.amalitech.kanbantaskmanagement.dto.response.user.UserProfileResponse;


public interface UserProfileService {
    UserProfileResponse getProfile();

    UserProfileResponse updateProfile(UserProfileUpdateRequest request);
}
