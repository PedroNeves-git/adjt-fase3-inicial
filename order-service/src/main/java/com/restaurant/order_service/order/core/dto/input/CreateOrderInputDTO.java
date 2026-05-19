package com.restaurant.order_service.order.core.dto.input;

import java.util.List;
import java.util.UUID;

public record CreateOrderInputDTO(
        Long clientId,
        UUID restaurantId,
        List<CreateOrderItemInputDTO> items
) { }
