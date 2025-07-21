package com.studybuddy.backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.AuthResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.SignupRequest;
import com.studybuddy.backend.entity.UserDetails;
import com.studybuddy.backend.exception.ResourceAlreadyExistsException;
import com.studybuddy.backend.repository.UserRepository;
import com.studybuddy.backend.utility.JwtUtil;
import com.studybuddy.backend.utility.VerificationCodeGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeGenerator codeGenerator;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Allows the user to sign up using email and a username.
     * 
     * @param req - The sign up request.
     */
    public void signup(SignupRequest req) {
        checkUserExists(req.getEmail(), req.getUsername());

        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // Generate verification code.
        String code = codeGenerator.generateCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        UserDetails user = new UserDetails(req.getEmail(), req.getUsername(), encodedPassword);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(expiry);

        userRepository.save(user);

        // Send verification email.
        emailService.sendVerificationEmail(req.getEmail(), code);
    }

    /**
     * Allows the user to login using email or username.
     * 
     * @param req - The login request.
     * @return User Details if passwords match.
     */
    public Optional<UserDetails> login(LoginRequest req) {
        return userRepository.findByUsername(req.getLogin()).or(() -> userRepository.findByEmail(req.getLogin()))
                .filter(u -> passwordEncoder.matches(req.getPassword(), u.getPassword()));
    }

    /**
     * Check if the user already exists in the database.
     * 
     * @param email    - Email entered in the request.
     * @param username - Username enetered in the request.
     */
    private void checkUserExists(String email, String username) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already exists.");
        }
    }

    /**
     * Verifies the user's email with the provided code.
     * 
     * @param email - User's email.
     * @param code  - 6-digit verification code.
     * @return True if user exists and has been verified.
     */
    public boolean verifyEmail(String email, String code) {
        Optional<UserDetails> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty())
            return false;

        UserDetails user = userOpt.get();

        if (user.isVerified())
            return true;

        // Check if code matches verification code.
        if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)
                && user.getVerificationCodeExpiry().isAfter(LocalDateTime.now())) {
            user.setVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiry(null);

            userRepository.save(user);
            return true;
        }

        return false;
    }

    /**
     * Resends a new verification code with a new expiry to the user's email.
     * 
     * @param email - User's email.
     */
    public void resendVerificationCode(String email) {
        Optional<UserDetails> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty())
            throw new RuntimeException("User not found, try a different email.");
        if (userOpt.get().isVerified())
            throw new RuntimeException("User already verified.");

        UserDetails user = userOpt.get();
        String newCode = codeGenerator.generateCode();
        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(5);

        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiry(newExpiry);

        userRepository.save(user);
        emailService.sendVerificationEmail(email, newCode);
    }

    /**
     * Refreshes the user's access token to avoid multiple logins.
     * 
     * @param refreshToken - User's old refresh token.
     * @return A new response with the user's new access and refresh tokens.
     */
    public AuthResponse refreshToken(String refreshToken) {
        // Extract username from refresh token.
        String username = jwtUtil.extractUsername(refreshToken);

        // Check token validity.
        if (!jwtUtil.validateToken(refreshToken, username))
            throw new RuntimeException("Invalid or expired refresh token");

        // Load user details.
        UserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new tokens.
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        // Return tokens with user info.
        return new AuthResponse(newAccessToken, newRefreshToken, user.getEmail(), username);
    }

    /**
     * Sends a verification code to the user's email if they try to login but are
     * unverified.
     * 
     * @param user - The user to send the verificaition code to.
     */
    public void handleUnverifiedUser(UserDetails user) {
        try {
            resendVerificationCode(user.getEmail());
        } catch (Exception e) {
            log.error("Failed to resend verification code during login", e);
        }
    }

    /**
     * Generates access and refresh tokens for users who have successfully logged
     * in.
     * 
     * @param user - User who has logged in.
     * @return A response that contains the user's tokens, email, and username.
     */
    public AuthResponse generateTokensForUser(UserDetails user) {
        String username = user.getUsername();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), username);
    }
}
