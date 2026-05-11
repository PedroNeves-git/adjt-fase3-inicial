package com.restaurant.auth_service.core.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email) {
        super("Email already exists: " + email);
    }
}
