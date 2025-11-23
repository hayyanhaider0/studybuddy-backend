package com.studybuddy.backend.dto.auth;

import com.studybuddy.backend.enums.auth.EducationLevel;
import com.studybuddy.backend.enums.auth.Occupation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private String username;
    private String displayName;
    private Occupation occupation;
    private EducationLevel educationLevel;
}
