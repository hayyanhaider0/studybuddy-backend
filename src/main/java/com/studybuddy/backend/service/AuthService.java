package com.studybuddy.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.studybuddy.backend.dto.LoginRequest;
import com.studybuddy.backend.dto.SignupRequest;
import com.studybuddy.backend.entity.UserDetails;
import com.studybuddy.backend.exception.ResourceAlreadyExistsException;
import com.studybuddy.backend.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Allows the user to sign up using email and a username.
     * 
     * @param req - The sign up request.
     */
    public void signup(SignupRequest req) {
        checkUserExists(req.getEmail(), req.getUsername());

        String encodedPassword = passwordEncoder.encode(req.getPassword());
        UserDetails user = new UserDetails(req.getEmail(), req.getUsername(), encodedPassword);
        userRepository.save(user);
    }

    /**
     * Allows the user to login using email or username.
     * 
     * @param req - The login request.
     * @return User Details if passwords match.
     */
    public Optional<UserDetails> login(LoginRequest req) {
        // Find the user in the database.
        Optional<UserDetails> user = userRepository.findByUsername(req.getLogin());
        if (user.isEmpty())
            user = userRepository.findByEmail(req.getLogin());
        // Return the user if passwords match.
        return user.filter(u -> passwordEncoder.matches(req.getPassword(), u.getPassword()));
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
