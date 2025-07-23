package com.studybuddy.backend.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserDetails {
    @Id
    private String id;

    private String email;
    private String username;
    private String password;
    private String role;
    private boolean verified;

    // User verification fields
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;

    // Password reset fields
    private String resetCode;
    private LocalDateTime resetCodeExpiry;

    public UserDetails(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = "USER";
        this.verified = false;
    }
}
