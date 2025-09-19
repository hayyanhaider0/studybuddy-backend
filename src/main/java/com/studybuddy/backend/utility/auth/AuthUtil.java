package com.studybuddy.backend.utility.auth;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {
    public String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
