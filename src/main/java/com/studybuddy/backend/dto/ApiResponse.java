package com.studybuddy.backend.dto;

import com.studybuddy.backend.enums.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ErrorCode error;
    private String message;
}
