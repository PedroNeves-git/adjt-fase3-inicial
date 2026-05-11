package com.restaurant.auth_service.core.doman.vo;

import com.restaurant.auth_service.core.exception.InvalidFieldException;

public record Password(String value) {
    public Password(String value) {
        if (value == null) {
            throw new InvalidFieldException("password", "REQUIRED", "Password is required");
        }

        String normalized = value.trim();

        if (normalized.isBlank()) {
            throw new InvalidFieldException("password", "REQUIRED", "Password is required");
        }

        if (normalized.length() < 8) {
            throw new InvalidFieldException("password", "TOO_SHORT", "Password must be at least 8 characters");
        }

        if (normalized.length() > 64) {
            throw new InvalidFieldException("password", "TOO_LONG", "Password must be at most 64 characters");
        }

        if (!normalized.matches(".*[A-Z].*")) {
            throw new InvalidFieldException("password", "WEAK_PASSWORD", "Password must contain at least one uppercase letter");
        }

        if (!normalized.matches(".*[a-z].*")) {
            throw new InvalidFieldException("password", "WEAK_PASSWORD", "Password must contain at least one lowercase letter");
        }

        if (!normalized.matches(".*\\d.*")) {
            throw new InvalidFieldException("password", "WEAK_PASSWORD", "Password must contain at least one number");
        }

        this.value = normalized;
    }

    @Override
    public String toString() {
        return "Password(****)";
    }
}
