package com.amalitech.kanbantaskmanagement.service;

import com.amalitech.kanbantaskmanagement.dto.request.user.UserRegistrationRequest;
import com.amalitech.kanbantaskmanagement.model.jpa.User;

public interface UserService {
    User registerUser(UserRegistrationRequest request);
}
