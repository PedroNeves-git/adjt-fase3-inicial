package com.restaurant.order_service.order.infra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderItemRequest(
        @NotNull(message = "menuItemId is required")
        UUID menuItemId,

        @Min(value = 1, message = "quantity must be at least 1")
        int quantity
) { }
