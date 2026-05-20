package com.restaurant.order_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentClient {

    private CurrentClient() { }

    public static AuthenticatedClient get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedClient client)) {
            throw new IllegalStateException("No authenticated client in security context");
        }
        return client;
    }

    public static Long clientId() {
        return get().clientId();
    }
}
