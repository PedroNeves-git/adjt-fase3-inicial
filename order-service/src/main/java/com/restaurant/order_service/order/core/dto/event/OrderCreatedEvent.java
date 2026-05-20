package com.restaurant.order_service.order.core.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        @JsonProperty("eventId")       UUID eventId,
        @JsonProperty("eventType")     String eventType,
        @JsonProperty("timestamp")     Instant timestamp,
        @JsonProperty("pedidoId")      UUID orderId,
        @JsonProperty("clienteId")     Long clientId,
        @JsonProperty("restauranteId") UUID restaurantId,
        @JsonProperty("valorTotal")    BigDecimal totalAmount,
        @JsonProperty("itens")         List<Item> items
) {

    public static final String EVENT_TYPE = "PEDIDO_CRIADO";

    public record Item(
            @JsonProperty("produtoId")     UUID menuItemId,
            @JsonProperty("nome")          String name,
            @JsonProperty("quantidade")    int quantity,
            @JsonProperty("precoUnitario") BigDecimal unitPrice
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
