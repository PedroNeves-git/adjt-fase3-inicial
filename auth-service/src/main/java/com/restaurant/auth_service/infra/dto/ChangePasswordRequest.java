package com.restaurant.auth_service.infra.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {}
