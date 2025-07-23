package com.studybuddy.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.studybuddy.backend.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // CONFLICTS 409
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExists(ResourceAlreadyExistsException e) {
        log.warn("Resource already exists: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyVerified(UserAlreadyVerifiedException e) {
        log.warn("User already verified: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    // NOT FOUND 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    // BAD REQUEST 400
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(InvalidRequestException e) {
        log.warn("Invalid request: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // UNAUTHORIZED 401
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException e) {
        log.warn("Invalid token: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    // INTERNAL SERVER ERROR 500 / UNEXPECTED
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(Exception e) {
        log.error("Unexpected error occured", e);
        ApiResponse<Void> res = new ApiResponse<Void>(false, "An unexpected error occured", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
