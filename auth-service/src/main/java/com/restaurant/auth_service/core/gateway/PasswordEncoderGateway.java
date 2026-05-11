package com.restaurant.auth_service.core.gateway;

public interface PasswordEncoderGateway {
    String encode(String password);
}
