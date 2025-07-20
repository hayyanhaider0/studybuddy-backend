package com.studybuddy.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.studybuddy.backend.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceExists(ResourceAlreadyExistsException e) {
        ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }
}
