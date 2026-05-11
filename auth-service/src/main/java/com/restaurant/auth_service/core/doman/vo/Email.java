package com.restaurant.auth_service.core.doman.vo;

import com.restaurant.auth_service.core.exception.InvalidFieldException;

import java.util.regex.Pattern;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Email(String value) {
        if (value == null) {
            throw new InvalidFieldException("email", "REQUIRED", "Email is required");
        }

        String normalized = value.trim().toLowerCase();

        if (normalized.isBlank()) {
            throw new InvalidFieldException("email", "REQUIRED", "Email is required");
        }

        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidFieldException("email", "INVALID_FORMAT", "Invalid email");
        }

        this.value = normalized;
    }
}
