package com.restaurant.auth_service.core.dto.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.restaurant.auth_service.core.doman.enums.Role;

import java.time.LocalDateTime;

public record UserOutputDTO(
        Long id,
        String name,
        String email,
        Role role,
        boolean active,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime updatedAt

) {
}
