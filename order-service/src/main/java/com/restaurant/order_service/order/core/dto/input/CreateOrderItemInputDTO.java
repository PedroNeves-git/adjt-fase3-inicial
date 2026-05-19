package com.restaurant.order_service.order.core.dto.input;

import java.util.UUID;

public record CreateOrderItemInputDTO(
        UUID menuItemId,
        int quantity
) { }
