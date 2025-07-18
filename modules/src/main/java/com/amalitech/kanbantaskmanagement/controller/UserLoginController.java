package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.dto.request.*;
import com.amalitech.kanbantaskmanagement.dto.response.*;
import com.amalitech.kanbantaskmanagement.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UserLoginController {

    private final AuthServiceImpl authService;

    @Autowired
    public UserLoginController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response,"OTP Sent!"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response,"Login Successful!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "Logged out successfully!"));
    }
}
