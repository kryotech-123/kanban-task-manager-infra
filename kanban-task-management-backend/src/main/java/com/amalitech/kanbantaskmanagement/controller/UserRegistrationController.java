package com.amalitech.kanbantaskmanagement.controller;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserRegistrationRequest;
import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;
import com.amalitech.kanbantaskmanagement.model.jpa.User;
import com.amalitech.kanbantaskmanagement.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserRegistrationController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
       User user =  userServiceImpl.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "User registered successfully"));
    }
}

