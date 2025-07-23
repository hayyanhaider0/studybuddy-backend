package com.studybuddy.backend.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.ApiResponse;
import com.studybuddy.backend.dto.AuthResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.ResetPasswordRequest;
import com.studybuddy.backend.dto.SignupRequest;
import com.studybuddy.backend.dto.CodeRequest;
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
        Map<String, String> resData = authService.signup(req);
        ApiResponse<?> res = new ApiResponse<>(true, "User registered successfully.", resData);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email", description = "Sends an email to the user's email to verify their account.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody CodeRequest req) {
        authService.verifyEmail(req.getEmail(), req.getCode());
        ApiResponse<Void> res = new ApiResponse<>(true, "User verified successfully.", null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code", description = "Resends a new 6-digit verification code to the user's email")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody CodeRequest req) {
        authService.resendVerificationCode(req.getEmail());
        ApiResponse<Void> res = new ApiResponse<>(true, "Verification code resent successfully.", null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset Study Buddy password", description = "Sends a 6-digit OTP allowing the user to reset their password")
    public ResponseEntity<ApiResponse<?>> reset(@RequestBody Map<String, String> payload) {
        Map<String, String> resData = authService.sendResetCode(payload.get("login"));
        ApiResponse<?> res = new ApiResponse<>(true, "Reset code send successfully.", resData);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/verify-reset")
    @Operation(summary = "Verified user's password reset code", description = "Compares the code entered with the reset code in the database, and checks its expiry time")
    public ResponseEntity<ApiResponse<?>> verifyReset(@RequestBody CodeRequest req) {
        authService.verifyResetCode(req.getEmail(), req.getCode());
        ApiResponse<Void> res = new ApiResponse<>(true, "Code verified successfully.", null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Resets the user's Study Buddy password", description = "Sets the user's password to a new password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getPassword(), req.getConfirmPassword());
        ApiResponse<Void> res = new ApiResponse<>(true, "Password changed successfully.", null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to Study Buddy", description = "Logs a registered user in to Study Buddy with a JWT token")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest req) {
        Object loginResult = authService.login(req);

        if (loginResult instanceof AuthResponse) {
            ApiResponse<AuthResponse> res = new ApiResponse<>(true, "Logged in successfully.",
                    (AuthResponse) loginResult);
            return ResponseEntity.ok(res);
        } else {
            // Unverified user case - service returns email map
            @SuppressWarnings("unchecked")
            Map<String, String> emailData = (Map<String, String>) loginResult;
            ApiResponse<?> res = new ApiResponse<>(true, "Please verify your email.", emailData);
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes the user's access token to skip login.")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody Map<String, String> req) {
        AuthResponse authResponse = authService.refreshToken(req.get("refreshToken"));
        ApiResponse<AuthResponse> res = new ApiResponse<>(true, "Token refreshed successfully.", authResponse);
        return ResponseEntity.ok(res);
    }
}
