package com.restaurant.order_service.order.core.dto.output;

import com.restaurant.order_service.order.core.domain.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemOutputDTO(
        UUID id,
        UUID menuItemId,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
    public static OrderItemOutputDTO from(OrderItem item) {
        return new OrderItemOutputDTO(
                item.getId(),
                item.getMenuItemId(),
                item.getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.calculateSubtotal()
        );
    }
}
