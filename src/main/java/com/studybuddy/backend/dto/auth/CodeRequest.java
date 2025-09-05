package com.studybuddy.backend.dto;

import lombok.Data;

@Data
public class CodeRequest {
    private String email;
    private String code;
}
