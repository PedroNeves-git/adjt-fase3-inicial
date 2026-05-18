package com.restaurant.order_service.order.core.dto.event;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        Instant timestamp,
        UUID orderId,
        Long clientId,
        UUID restaurantId,
        BigDecimal totalAmount,
        List<Item> items
) {

    public static final String EVENT_TYPE = "ORDER_CREATED";

    public record Item(
            UUID menuItemId,
            String name,
            int quantity,
            BigDecimal unitPrice
    ) { }

    public static OrderCreatedEvent from(Order order) {
        List<Item> items = order.getItems().stream()
                .map(OrderCreatedEvent::toEventItem)
                .toList();

        return new OrderCreatedEvent(
                UUID.randomUUID(),
                EVENT_TYPE,
                Instant.now(),
                order.getId(),
                order.getClientId(),
                order.getRestaurantId(),
                order.calculateTotal(),
                items
        );
    }

    private static Item toEventItem(OrderItem item) {
        return new Item(item.getMenuItemId(), item.getName(), item.getQuantity(), item.getUnitPrice());
    }
}
