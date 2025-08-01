package com.studybuddy.backend.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.studybuddy.backend.entity.embedded.UserPreferences;
import com.studybuddy.backend.entity.embedded.UserSecurity;
import com.studybuddy.backend.enums.EducationLevel;
import com.studybuddy.backend.enums.Occupation;
import com.studybuddy.backend.enums.Role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {
    // Core auth
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String username;

    private String passwordHash;
    private boolean verified;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    // Account settings
    private String displayName;
    private Role role;
    private Occupation occupation;
    private EducationLevel educationLevel;

    // Device/Security
    private UserSecurity security;
    private UserPreferences preferences;

    public User(String email, String username, String displayName, String passwordHash) {
        this.email = email.trim().toLowerCase();
        this.username = username.trim().toLowerCase();
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
        this.verified = false;
        this.security = new UserSecurity();
        this.preferences = new UserPreferences();
    }
}
