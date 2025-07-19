package com.studybuddy.backend.validation;

import com.studybuddy.backend.dto.SignupRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignupRequest> {
    @Override
    public boolean isValid(SignupRequest req, ConstraintValidatorContext ctx) {
        return req.getPassword() != null && req.getPassword().equals(req.getConfirmPassword());
    }
}
