package com.studybuddy.backend.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mongodb.DuplicateKeyException;
import com.studybuddy.backend.dto.ApiResponse;
import com.studybuddy.backend.enums.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // CONFLICTS 409
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceAlreadyExists(ResourceAlreadyExistsException e) {
        log.warn("Resource already exists exception: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.RESOURCE_ALREADY_EXISTS, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyVerified(UserAlreadyVerifiedException e) {
        log.warn("User already verified exception: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.USER_ALREADY_VERIFIED, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKey(DuplicateKeyException e) {
        String msg = "User already exists";
        String errMsg = e.getMessage().toLowerCase();

        if (errMsg.contains("username"))
            msg = "Username already exists.";
        if (errMsg.contains("email"))
            msg = "Email already exists.";
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.RESOURCE_ALREADY_EXISTS, msg);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    // NOT FOUND 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found exception: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.RESOURCE_NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    // BAD REQUEST 400
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequest(InvalidRequestException e) {
        log.warn("Invalid request exception: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.INVALID_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    // UNAUTHORIZED 401
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleEmailNotVerifiedException(
            EmailNotVerifiedException e) {
        log.warn("Email not verified: {}", e.getMessage());
        Map<String, String> resData = Map.of("email", e.getEmail());
        ApiResponse<Map<String, String>> res = new ApiResponse<Map<String, String>>(false, resData,
                ErrorCode.EMAIL_NOT_VERIFIED, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException e) {
        log.warn("Invalid token: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.INVALID_TOKEN, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    // INTERNAL SERVER ERROR 500 / UNEXPECTED
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedError(Exception e) {
        log.error("Unexpected error occured: {}", e.getMessage());
        ApiResponse<Void> res = new ApiResponse<Void>(false, null, ErrorCode.UNEXPECTED_ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
