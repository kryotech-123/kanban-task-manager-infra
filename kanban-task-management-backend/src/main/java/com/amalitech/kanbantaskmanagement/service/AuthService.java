package com.amalitech.kanbantaskmanagement.service;

import com.amalitech.kanbantaskmanagement.dto.response.AuthResponse;
import com.amalitech.kanbantaskmanagement.dto.request.LoginRequest;
import com.amalitech.kanbantaskmanagement.dto.response.LoginResponse;
import com.amalitech.kanbantaskmanagement.dto.request.OtpVerificationRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    AuthResponse verifyOtp(OtpVerificationRequest request);
    void logout();
}