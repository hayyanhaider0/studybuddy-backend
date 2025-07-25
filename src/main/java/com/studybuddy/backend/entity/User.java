package com.studybuddy.backend.entity;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private Instant createdAt;
    private Instant updatedAt;

    // Account settings
    private String displayName;
    private Role role;
    private Occupation occupation;
    private EducationLevel educationLevel;
    private Map<String, Object> preferences;
    private boolean notificationsEnabled;
    private String timeZone;

    // Device/Security
    private Instant lastLoginAt;
    private int loginCount;
    // private boolean twoFactorEnabled;
    // private String[] devices;

    // User verification fields
    private String verificationCode;
    private Instant verificationCodeExpiry;

    // Password reset fields
    private String resetCode;
    private Instant resetCodeExpiry;

    public User(String email, String username, String displayName, String passwordHash) {
        this.email = email.trim().toLowerCase();
        this.username = username.trim().toLowerCase();
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
        this.verified = false;

        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.timeZone = ZoneId.systemDefault().getId();
    }
}
