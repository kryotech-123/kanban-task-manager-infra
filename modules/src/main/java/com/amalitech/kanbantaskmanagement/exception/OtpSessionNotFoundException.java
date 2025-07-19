package com.amalitech.kanbantaskmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OtpSessionNotFoundException extends RuntimeException {
    public OtpSessionNotFoundException(String message) {
        super(message);
    }
}