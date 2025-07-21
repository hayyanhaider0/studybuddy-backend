package com.studybuddy.backend.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.ApiResponse;
import com.studybuddy.backend.dto.AuthResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.SignupRequest;
import com.studybuddy.backend.dto.VerificationRequest;
import com.studybuddy.backend.entity.UserDetails;
import com.studybuddy.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register user", description = "Creates a new user with email, username, and password.")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        Map<String, String> resData = Collections.singletonMap("email", req.getEmail());
        ApiResponse<?> res = new ApiResponse<>(true, "User registered successfully!", resData);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email", description = "Sends an email to the user's email to verify their account.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody VerificationRequest req) {
        boolean verified = authService.verifyEmail(req.getEmail(), req.getCode());

        if (verified) {
            ApiResponse<Void> res = new ApiResponse<>(true, "User verified successfully.", null);
            return ResponseEntity.ok(res);
        } else {
            ApiResponse<Void> res = new ApiResponse<Void>(false,
                    "Invalid code or the code has expired. Please retry by resending code.", null);
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code", description = "Resends a new 6-digit verification code to the user's email")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody VerificationRequest req) {
        try {
            String email = req.getEmail();
            authService.resendVerificationCode(email);
            ApiResponse<Void> res = new ApiResponse<>(true, "Verification code resent successfully.", null);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            ApiResponse<Void> res = new ApiResponse<Void>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login to Study Buddy", description = "Logs a registered user in to Study Buddy with a JWT token.")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest req) {
        Optional<UserDetails> userOpt = authService.login(req);

        if (userOpt.isEmpty()) {
            // Invalid username/password
            ApiResponse<UserDetails> res = new ApiResponse<UserDetails>(false, "Invalid username or password", null);
            return ResponseEntity.status(404).body(res);
        }

        UserDetails user = userOpt.get();

        if (!user.isVerified()) {
            // Resend verification
            authService.handleUnverifiedUser(user);
            Map<String, String> resData = Collections.singletonMap("email", user.getEmail());
            ApiResponse<?> res = new ApiResponse<>(true, "Please verify your email.", resData);
            return ResponseEntity.ok(res);
        }

        // If the user has logged in successfully, generate tokens.
        AuthResponse authResponse = authService.generateTokensForUser(user);

        ApiResponse<AuthResponse> res = new ApiResponse<AuthResponse>(true, "Logged in successfully.", authResponse);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes the user's access token to skip login.")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");

        // Check validity.
        if (refreshToken == null || refreshToken.isEmpty()) {
            ApiResponse<Map<String, String>> res = new ApiResponse<>(false, "Refresh token is expired", null);
            return ResponseEntity.badRequest().body(res);
        }

        // If valid, send an ok response.
        try {
            AuthResponse authResponse = authService.refreshToken(refreshToken);
            ApiResponse<AuthResponse> res = new ApiResponse<>(true, "Refresh token is expired", authResponse);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            // Catch other errors.
            ApiResponse<Void> res = new ApiResponse<>(false, "Invalid or expired refresh token", null);
            return ResponseEntity.status(401).body(res);
        }
    }
}
