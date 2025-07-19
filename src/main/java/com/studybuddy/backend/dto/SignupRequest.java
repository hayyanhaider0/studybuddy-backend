package com.studybuddy.backend.dto;

import com.studybuddy.backend.validation.PasswordMatches;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@PasswordMatches
public class SignupRequest {
    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Username is required.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter."),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter."),
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number."),
            @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain a special character (!@#$%^&*()).")
    })
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;
}
