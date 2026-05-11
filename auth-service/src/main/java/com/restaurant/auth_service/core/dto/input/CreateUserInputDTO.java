package com.restaurant.auth_service.core.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserInputDTO(
    @Schema(example = "John Doe") String name,
    @Schema(example = "john@email.com") String email,
    @Schema(example = "Password@123") String password,
    @Schema(example = "CLIENT") String role
) { }
