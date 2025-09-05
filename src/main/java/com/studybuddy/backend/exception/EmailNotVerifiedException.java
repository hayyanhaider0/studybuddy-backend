package com.studybuddy.backend.exception;

public class EmailNotVerifiedException extends RuntimeException {
    private final String email;

    public EmailNotVerifiedException(String email, String msg) {
        super(msg);
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
