package com.studybuddy.backend.service.auth;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.studybuddy.backend.dto.auth.AuthResponse;
import com.studybuddy.backend.dto.auth.LoginRequest;
import com.studybuddy.backend.dto.auth.SignupRequest;
import com.studybuddy.backend.entity.auth.User;
import com.studybuddy.backend.entity.auth.embedded.UserSecurity;
import com.studybuddy.backend.exception.EmailNotVerifiedException;
import com.studybuddy.backend.exception.InvalidRequestException;
import com.studybuddy.backend.exception.InvalidTokenException;
import com.studybuddy.backend.exception.ResourceAlreadyExistsException;
import com.studybuddy.backend.exception.ResourceNotFoundException;
import com.studybuddy.backend.exception.UserAlreadyVerifiedException;
import com.studybuddy.backend.repository.UserRepository;
import com.studybuddy.backend.utility.auth.JwtUtil;
import com.studybuddy.backend.utility.auth.VerificationCodeGenerator;

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
        final String normalizedEmail = normalizeString(req.getEmail());
        final String normalizedUsername = normalizeString(req.getUsername());

        checkUserExists(normalizedEmail, normalizedUsername);
        String encodedPassword = passwordEncoder.encode(req.getPassword());

        // Generate verification code.
        String code = codeGenerator.generateCode();
        Instant expiry = Instant.now().plusSeconds(SECONDS_TO_MINUTES * 5);

        User user = new User(normalizedEmail, normalizedUsername, req.getUsername(), encodedPassword);

        UserSecurity security = user.getSecurity();
        security.setVerificationCode(code);
        security.setVerificationCodeExpiry(expiry);

        // Check if the user already exists.
        try {
            userRepository.save(user);
        } catch (DuplicateKeyException e) {
            String msg = "User already exists";
            String errMsg = e.getMessage().toLowerCase();

            if (errMsg.contains("username"))
                msg = "Username already exists.";
            if (errMsg.contains("email"))
                msg = "Email already exists.";
            throw new ResourceAlreadyExistsException(msg);
        }

        // Send verification email.
        emailService.sendCodeEmail(normalizedEmail, code, "Verification");

        return Collections.singletonMap("email", normalizedEmail);
    }

    /**
     * Allows the user to login using email or username.
     * 
     * @param req - The login request.
     * @return User Details if passwords match.
     * @throws EmailNotVerifiedException if user exists but email not verified.
     */
    public AuthResponse login(LoginRequest req) {
        final String login = normalizeString(req.getLogin());

        Optional<User> userOpt = userRepository.findByUsername(login)
                .or(() -> userRepository.findByEmail(login));
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new InvalidRequestException("Invalid username or password.");

        if (!user.isVerified()) {
            handleUnverifiedUser(user);
            throw new EmailNotVerifiedException(user.getEmail(), "Please verify your email before logging in.");
        }

        UserSecurity security = user.getSecurity();
        security.setLoginCount(security.getLoginCount() + 1);
        security.setLastLoginAt(Instant.now());
        userRepository.save(user);

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
        final String normalizedEmail = normalizeString(email);

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Email already verified.
        if (user.isVerified())
            return;

        // Check if code matches verification code.
        UserSecurity security = user.getSecurity();
        if (security.getVerificationCode() == null || !security.getVerificationCode().equals(code)
                || security.getVerificationCodeExpiry().isBefore(Instant.now()))
            throw new InvalidRequestException("Invalid code or the code has expired.");

        // Update user
        user.setVerified(true);
        security.setVerificationCode(null);
        security.setVerificationCodeExpiry(null);
        userRepository.save(user);
    }

    /**
     * Resends a new verification code with a new expiry to the user's email.
     * 
     * @param email - User's email.
     */
    public void resendVerificationCode(String email) {
        final String normalizedEmail = normalizeString(email);

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));
        if (user.isVerified())
            throw new UserAlreadyVerifiedException("User is already verified.");

        String newCode = codeGenerator.generateCode();
        Instant newExpiry = Instant.now().plusSeconds(SECONDS_TO_MINUTES * 5);

        // Update user
        UserSecurity security = user.getSecurity();
        security.setVerificationCode(newCode);
        security.setVerificationCodeExpiry(newExpiry);
        userRepository.save(user);

        emailService.sendCodeEmail(normalizedEmail, newCode, "Verification");
    }

    /**
     * Sends a 6-digit OTP allowing the user to reset their password.
     * 
     * @param login - User's username or email.
     */
    public Map<String, String> sendResetCode(String login) {
        final String normalizedLogin = normalizeString(login);

        Optional<User> userOpt = userRepository.findByEmail(normalizedLogin)
                .or(() -> userRepository.findByUsername(normalizedLogin));

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String code = codeGenerator.generateCode();
        Instant expiry = Instant.now().plusSeconds(SECONDS_TO_HOUR);

        // Update user
        UserSecurity security = user.getSecurity();
        security.setResetCode(code);
        security.setResetCodeExpiry(expiry);
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
        final String normalizedEmail = normalizeString(email);

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));
        UserSecurity security = user.getSecurity();

        if (security.getResetCode() == null || !security.getResetCode().equals(code)
                || security.getResetCodeExpiry().isBefore(Instant.now()))
            throw new InvalidRequestException("Invalid or expired reset code.");

        // Update user
        security.setResetCode(null);
        security.setResetCodeExpiry(null);
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
        final String normalizedEmail = normalizeString(email);

        if (!password.equals(confirmPassword))
            throw new InvalidRequestException("Password and confirm password must be the same.");

        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (passwordEncoder.matches(password, user.getPasswordHash()))
            throw new InvalidRequestException("New password can not be the same as the old password.");

        // Update user
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
        String id = jwtUtil.extractId(refreshToken);

        // Check token validity.
        if (!jwtUtil.validateToken(refreshToken, id))
            throw new InvalidTokenException("Invalid or expired refresh token");

        // Load user details.
        Optional<User> userOpt = userRepository.findByUsername(id);
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found."));

        // Generate new tokens.
        String newAccessToken = jwtUtil.generateAccessToken(id);
        String newRefreshToken = jwtUtil.generateRefreshToken(id);

        // Return tokens with user info.
        return new AuthResponse(newAccessToken, newRefreshToken, user.getEmail(), user.getUsername(),
                user.getDisplayName());
    }

    /**
     * Sends a verification code to the user's email if they try to login but are
     * unverified.
     * 
     * @param user - The user to send the verificaition code to.
     */
    private void handleUnverifiedUser(User user) {
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
        String id = user.getId();
        String accessToken = jwtUtil.generateAccessToken(id);
        String refreshToken = jwtUtil.generateRefreshToken(id);
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getUsername(), user.getDisplayName());
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
     * Converts the original string into a trimmed and lowercase version.
     * 
     * @param s - Original string.
     * @return Trimmed and lowercase original string.
     */
    private static String normalizeString(String s) {
        return s.trim().toLowerCase();
    }
}
