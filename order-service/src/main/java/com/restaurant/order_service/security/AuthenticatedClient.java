package com.restaurant.order_service.security;

public record AuthenticatedClient(
        Long clientId,
        String email,
        String role
) { }
