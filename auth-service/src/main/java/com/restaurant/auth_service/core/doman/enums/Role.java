package com.restaurant.auth_service.core.doman.enums;

import com.restaurant.auth_service.core.exception.InvalidRoleException;
import lombok.Getter;

public enum Role {
    CLIENT("client"),
    ADMIN("admin");

    @Getter
    private String value;

    Role(String value) {
        this.value = value;
    }

    public static Role from(String raw) {
        if (raw == null) {
            throw new InvalidRoleException("null");
        }

        return switch (raw.trim().toUpperCase()) {
            case "ADMIN" -> ADMIN;
            case "CLIENT" -> CLIENT;
            default -> throw new InvalidRoleException(raw);
        };
    }
}
