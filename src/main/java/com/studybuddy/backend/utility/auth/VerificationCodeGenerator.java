package com.studybuddy.backend.utility;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
