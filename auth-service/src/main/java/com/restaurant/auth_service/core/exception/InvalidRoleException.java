package com.restaurant.auth_service.core.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String value) {
        super("Invalid role: " + value + ". Must be ADMIN or CLIENT.");
    }
}
