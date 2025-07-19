package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserProfileUpdateRequest;
import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;
import com.amalitech.kanbantaskmanagement.dto.response.user.UserProfileResponse;
import com.amalitech.kanbantaskmanagement.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@RestController
public class UserProfileController {
    private final UserProfileService userProfileServiceImpl;

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        UserProfileResponse profile = userProfileServiceImpl.getProfile();
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(profile, "Profile fetched successfully"));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse updated = userProfileServiceImpl.updateProfile(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Profile updated successfully"));
    }

}
