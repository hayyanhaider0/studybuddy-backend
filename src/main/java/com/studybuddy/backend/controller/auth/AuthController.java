package com.studybuddy.backend.controller.auth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.auth.ApiResponse;
import com.studybuddy.backend.dto.auth.AuthResponse;
import com.studybuddy.backend.dto.auth.CodeRequest;
import com.studybuddy.backend.dto.auth.LoginRequest;
import com.studybuddy.backend.dto.auth.ResetPasswordRequest;
import com.studybuddy.backend.dto.auth.SignupRequest;
import com.studybuddy.backend.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Register user", description = "Creates a new user with email, username, and password.")
    public ResponseEntity<ApiResponse<Map<String, String>>> signup(@Valid @RequestBody SignupRequest req) {
        Map<String, String> resData = authService.signup(req);
        ApiResponse<Map<String, String>> res = new ApiResponse<Map<String, String>>(true, resData, null,
                "User registered successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user's email", description = "Sends an email to the user's email to verify their account.")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@Valid @RequestBody CodeRequest req) {
        authService.verifyEmail(req.getEmail(), req.getCode());
        ApiResponse<Void> res = new ApiResponse<Void>(true, null, null, "User verified successfully.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification code", description = "Resends a new 6-digit verification code to the user's email")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestBody CodeRequest req) {
        authService.resendVerificationCode(req.getEmail());
        ApiResponse<Void> res = new ApiResponse<Void>(true, null, null, "Verification code resent successfully.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset Study Buddy password", description = "Sends a 6-digit OTP allowing the user to reset their password")
    public ResponseEntity<ApiResponse<Map<String, String>>> reset(@RequestBody Map<String, String> payload) {
        Map<String, String> resData = authService.sendResetCode(payload.get("login"));
        ApiResponse<Map<String, String>> res = new ApiResponse<Map<String, String>>(true, resData, null,
                "Reset code send successfully.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }

    @PostMapping("/verify-reset")
    @Operation(summary = "Verify user's password reset code", description = "Compares the code entered with the reset code in the database, and checks its expiry time")
    public ResponseEntity<ApiResponse<Void>> verifyReset(@RequestBody CodeRequest req) {
        authService.verifyResetCode(req.getEmail(), req.getCode());
        ApiResponse<Void> res = new ApiResponse<Void>(true, null, null, "Code verified successfully.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Resets the user's Study Buddy password", description = "Sets the user's password to a new password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.getEmail(), req.getPassword(), req.getConfirmPassword());
        ApiResponse<Void> res = new ApiResponse<Void>(true, null, null, "Password changed successfully.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to Study Buddy", description = "Logs a registered user in to Study Buddy with a JWT token")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse resData = authService.login(req);
        ApiResponse<AuthResponse> res = new ApiResponse<AuthResponse>(true, resData, null, "Logged in successfully.");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh user's access token", description = "Refreshes the user's access token to skip login.")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> req) {
        AuthResponse authResponse = authService.refreshToken(req.get("refreshToken"));
        ApiResponse<AuthResponse> res = new ApiResponse<AuthResponse>(true, authResponse, null,
                "Token refreshed successfully.");
        return ResponseEntity.ok(res);
    }
}
