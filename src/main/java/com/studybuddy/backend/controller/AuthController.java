package com.studybuddy.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studybuddy.backend.dto.ApiResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.SignupRequest;
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
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        ApiResponse<Void> res = new ApiResponse<>(true, "User registered successfully!", null);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    @Operation(summary = "Login to Study Buddy", description = "Logs a registered user in to Study Buddy with a JWT token.")
    public ResponseEntity<ApiResponse<UserDetails>> login(@RequestBody LoginRequest req) {
        Optional<UserDetails> user = authService.login(req);
        if (user.isPresent()) {
            ApiResponse<UserDetails> res = new ApiResponse<>(true, "Logged in successfully.", user.get());
            return ResponseEntity.ok(res);
        } else {
            ApiResponse<UserDetails> res = new ApiResponse<>(false, "Invalid password.", null);
            return ResponseEntity.status(401).body(res);
        }
    }
}
