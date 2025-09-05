package com.studybuddy.backend.entity.embedded;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSecurity {
    // User verification fields
    private String verificationCode;
    private Instant verificationCodeExpiry;

    // Password reset fields
    private String resetCode;
    private Instant resetCodeExpiry;

    private Instant lastLoginAt;
    private int loginCount;
}
