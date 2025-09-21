package com.studybuddy.backend.utility.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.repository.UserRepository;

@Component
public class AuthUtil {
    private final UserRepository userRepository;
    private static final ThreadLocal<String> cachedUserId = new ThreadLocal<>();

    public AuthUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUserId() {
        String userId = cachedUserId.get();
        if (userId != null)
            return userId;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof String id)) {
            throw new ResourceNotFoundException("No authenticated user found.");
        }

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found.");
        }

        cachedUserId.set(id);

        return id;
    }
}
