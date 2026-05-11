package com.restaurant.auth_service.core.dto;

import com.restaurant.auth_service.core.doman.enums.Role;

import java.time.LocalDateTime;

public record UserPersistenceDTO(
        Long id,
        String name,
        String email,
        String password,
        Role role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
