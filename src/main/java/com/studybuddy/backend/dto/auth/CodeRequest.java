package com.studybuddy.backend.dto.auth;

import lombok.Data;

@Data
public class CodeRequest {
    private String email;
    private String code;
}
