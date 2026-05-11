package com.restaurant.auth_service.core.dto.output;

public record LoginDTO(
        String email,
        String password
) { }
