package com.restaurant.order_service.order.infra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "restaurantId is required")
        UUID restaurantId,

        @NotEmpty(message = "items must not be empty")
        @Valid
        List<CreateOrderItemRequest> items
) { }
