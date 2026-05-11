package com.restaurant.auth_service.infra.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) { }
