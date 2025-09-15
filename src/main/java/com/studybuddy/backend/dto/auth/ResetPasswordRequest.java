package com.studybuddy.backend.dto.auth;

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
public class ResetPasswordRequest {
    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    @Size(max = 254, message = "Email can not be longer than 254 characters.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
    @Pattern.List({
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter."),
            @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter."),
            @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one number."),
            @Pattern(regexp = ".*[!@#$%^&*()].*", message = "Password must contain a special character (!@#$%^&*()).")
    })
    private String password;

    @NotBlank(message = "Please confirm your password.")
    @Size(max = 64, message = "Password can not be longer than 64 characters.")
    private String confirmPassword;
}
