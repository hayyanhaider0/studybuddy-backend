package com.studybuddy.backend.exception;

public class UserAlreadyVerifiedException extends RuntimeException {
    public UserAlreadyVerifiedException(String msg) {
        super(msg);
    }
}
