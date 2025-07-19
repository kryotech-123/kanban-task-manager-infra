package com.amalitech.kanbantaskmanagement.exception;


import com.amalitech.kanbantaskmanagement.dto.response.ApiError;
import com.amalitech.kanbantaskmanagement.dto.response.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(EmailAlreadyExistException ex) {
        ApiError error = new ApiError("email", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Email already exists", List.of(error)));
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameAlreadyExists(UsernameAlreadyExistException ex) {
        ApiError error = new ApiError("username", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Username already exists", List.of(error)));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        ApiError error = new ApiError("user", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found", List.of(error)));
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> BoardNotFound(UserNotFoundException ex) {
        ApiError error = new ApiError("board", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("board not found", List.of(error)));
    }

    @ExceptionHandler(ColumnNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> ColumnNotFound(UserNotFoundException ex) {
        ApiError error = new ApiError("column", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("column not found", List.of(error)));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfileNotFound(ProfileNotFoundException ex) {
        ApiError error = new ApiError("profile", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Profile not found", List.of(error)));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException ex) {
        ApiError error = new ApiError("currentPassword", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid password", List.of(error)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiError> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ApiError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Constraint violation", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex, WebRequest request) {
        ApiError error = new ApiError("internal", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error occurred", List.of(error)));
    }

}
