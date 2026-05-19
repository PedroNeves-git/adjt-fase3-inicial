package com.restaurant.order_service.order.core.dto.output;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderOutputDTO(
        UUID id,
        Long clientId,
        UUID restaurantId,
        List<OrderItemOutputDTO> items,
        BigDecimal totalAmount,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderOutputDTO from(Order order) {
        return new OrderOutputDTO(
                order.getId(),
                order.getClientId(),
                order.getRestaurantId(),
                order.getItems().stream().map(OrderItemOutputDTO::from).toList(),
                order.calculateTotal(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
