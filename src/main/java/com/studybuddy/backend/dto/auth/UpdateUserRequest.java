package com.studybuddy.backend.dto.auth;

import com.studybuddy.backend.entity.auth.embedded.UserPreferences;
import com.studybuddy.backend.entity.auth.embedded.UserSecurity;
import com.studybuddy.backend.enums.auth.EducationLevel;
import com.studybuddy.backend.enums.auth.Occupation;
import com.studybuddy.backend.enums.auth.Role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateUserRequest {
    // Core fields
    private String email;
    private String username;
    private String displayName;
    private Role role;
    private String passwordHash;

    // Enums
    private Occupation occupation;
    private EducationLevel educationLevel;

    // Nested objects
    private UserPreferences preferences;
    private UserSecurity security;

    // Soft delete
    private Boolean isDeleted;
}
