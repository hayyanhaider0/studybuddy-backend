package com.studybuddy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Please enter your username or email.")
    @Size(max = 254, message = "Email/Username can not be longer than 254 characters.")
    private String login;

    @NotBlank(message = "Please enter your password")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters.")
    private String password;
}
