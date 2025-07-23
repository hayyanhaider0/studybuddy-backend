package com.studybuddy.backend.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.AuthResponse;
import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.SignupRequest;
import com.studybuddy.backend.entity.User;
import com.studybuddy.backend.exception.InvalidRequestException;
import com.studybuddy.backend.exception.InvalidTokenException;
import com.studybuddy.backend.exception.ResourceAlreadyExistsException;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.exception.UserAlreadyVerifiedException;
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

    private final int SECONDS_TO_HOUR = 3600;
    private final int SECONDS_TO_MINUTES = 60;

    /**
     * Allows the user to sign up using email and a username.
     * 
     * @param req - The sign up request.
     */
    public Map<String, String> signup(SignupRequest req) {
        checkUserExists(req.getEmail(), req.getUsername());

        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // Generate verification code.
        String code = codeGenerator.generateCode();
        Instant expiry = Instant.now().plusSeconds(SECONDS_TO_MINUTES * 5);

        User user = new User(req.getEmail(), req.getUsername(), encodedPassword);
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(expiry);

        userRepository.save(user);

        // Send verification email.
        emailService.sendCodeEmail(req.getEmail(), code, "Verification");

        return Collections.singletonMap("email", req.getEmail());
    }

    /**
     * Allows the user to login using email or username.
     * 
     * @param req - The login request.
     * @return User Details if passwords match.
     */
    public Object login(LoginRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getLogin())
                .or(() -> userRepository.findByEmail(req.getLogin()));
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new InvalidRequestException("Invalid username or password.");

        if (!user.isVerified()) {
            handleUnverifiedUser(user);
            return Collections.singletonMap("email", user.getEmail());
        }

        return generateTokensForUser(user);
    }

    /**
     * Verifies the user's email with the provided code.
     * 
     * @param email - User's email.
     * @param code  - 6-digit verification code.
     * @return True if user exists and has been verified.
     */
    public void verifyEmail(String email, String code) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Email already verified.
        if (user.isVerified())
            return;

        // Check if code matches verification code.
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)
                || user.getVerificationCodeExpiry().isBefore(Instant.now()))
            throw new InvalidRequestException("Invalid code or the code has expired.");

        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);

        userRepository.save(user);
    }

    /**
     * Resends a new verification code with a new expiry to the user's email.
     * 
     * @param email - User's email.
     */
    public void resendVerificationCode(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (user.isVerified())
            throw new UserAlreadyVerifiedException("User is already verified.");

        String newCode = codeGenerator.generateCode();
        Instant newExpiry = Instant.now().plusSeconds(SECONDS_TO_MINUTES * 5);

        user.setVerificationCode(newCode);
        user.setVerificationCodeExpiry(newExpiry);

        userRepository.save(user);
        emailService.sendCodeEmail(email, newCode, "Verification");
    }

    /**
     * Sends a 6-digit OTP allowing the user to reset their password.
     * 
     * @param login - User's username or email.
     */
    public Map<String, String> sendResetCode(String login) {
        Optional<User> userOpt = userRepository.findByEmail(login)
                .or(() -> userRepository.findByUsername(login));

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String code = codeGenerator.generateCode();
        Instant expiry = Instant.now().plusSeconds(SECONDS_TO_HOUR);

        user.setResetCode(code);
        user.setResetCodeExpiry(expiry);
        userRepository.save(user);

        String email = user.getEmail();
        emailService.sendCodeEmail(email, code, "Reset");

        return Collections.singletonMap("email", email);
    }

    /**
     * Verifies the reset code entered by the user with the code stored in the
     * database.
     * 
     * @param email - Email of the user.
     * @param code  - Code entered by the user.
     * @return True if the code entered is correct.
     */
    public void verifyResetCode(String email, String code) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (user.getResetCode() == null || !user.getResetCode().equals(code)
                || user.getResetCodeExpiry().isBefore(Instant.now()))
            throw new InvalidRequestException("Invalid or expired reset code.");

        user.setResetCode(null);
        user.setResetCodeExpiry(null);

        userRepository.save(user);
    }

    /**
     * Sets the user's password to the new password entered by the user.
     * 
     * @param email           - Email of the user.
     * @param password        - New password entered by the user.
     * @param confirmPassword - Confirm password field entered by the user.
     */
    public void resetPassword(String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new InvalidRequestException("Password and confirm password must be the same.");

        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (passwordEncoder.matches(password, user.getPasswordHash()))
            throw new InvalidRequestException("New password can not be the same as the old password.");

        String newEncodedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(newEncodedPassword);

        userRepository.save(user);
    }

    /**
     * Refreshes the user's access token to avoid multiple logins.
     * 
     * @param refreshToken - User's old refresh token.
     * @return A new response with the user's new access and refresh tokens.
     */
    public AuthResponse refreshToken(String refreshToken) {
        // Check incoming token validity.
        if (refreshToken == null || refreshToken.isEmpty())
            throw new ResourceNotFoundException("Refresh token not found.");

        // Extract username from refresh token.
        String username = jwtUtil.extractUsername(refreshToken);

        // Check token validity.
        if (!jwtUtil.validateToken(refreshToken, username))
            throw new InvalidTokenException("Invalid or expired refresh token");

        // Load user details.
        Optional<User> userOpt = userRepository.findByUsername(username);
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

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
    public void handleUnverifiedUser(User user) {
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
    public AuthResponse generateTokensForUser(User user) {
        String username = user.getUsername();
        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), username);
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
}
