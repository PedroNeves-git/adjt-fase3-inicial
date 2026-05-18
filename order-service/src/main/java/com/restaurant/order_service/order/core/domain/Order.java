package com.restaurant.order_service.order.core.domain;

import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private UUID id;
    private final Long clientId;
    private final UUID restaurantId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(
            UUID id,
            Long clientId,
            UUID restaurantId,
            List<OrderItem> items,
            OrderStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.clientId = Objects.requireNonNull(clientId, "clientId is required");
        this.restaurantId = Objects.requireNonNull(restaurantId, "restaurantId is required");
        this.items = requireAtLeastOneItem(items);
        this.status = Objects.requireNonNull(status, "status is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public static Order newOrder(Long clientId, UUID restaurantId, List<OrderItem> items) {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
                null,
                clientId,
                restaurantId,
                new ArrayList<>(items),
                OrderStatus.AWAITING_CONFIRMATION,
                now,
                now
        );
    }

    public static Order restore(
            UUID id,
            Long clientId,
            UUID restaurantId,
            List<OrderItem> items,
            OrderStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Order(
                Objects.requireNonNull(id, "id is required on restore"),
                clientId,
                restaurantId,
                new ArrayList<>(items),
                status,
                createdAt,
                updatedAt
        );
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        ensureStatusIs(OrderStatus.AWAITING_CONFIRMATION, "confirm");
        this.status = OrderStatus.PENDING_PAYMENT;
        touch();
    }

    public void markAsPaid() {
        ensureStatusIs(OrderStatus.PENDING_PAYMENT, "markAsPaid");
        this.status = OrderStatus.PAID;
        touch();
    }

    public void markAsPending() {
        ensureStatusIs(OrderStatus.PENDING_PAYMENT, "markAsPending");
        touch();
    }

    public void cancel() {
        if (status == OrderStatus.PAID) {
            throw new InvalidOrderStateException("Paid order cannot be cancelled");
        }
        if (status == OrderStatus.CANCELLED) {
            return;
        }
        this.status = OrderStatus.CANCELLED;
        touch();
    }

    public boolean belongsTo(Long clientId) {
        return this.clientId.equals(clientId);
    }

    public void assignId(UUID id) {
        if (this.id != null) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = Objects.requireNonNull(id, "id is required");
    }

    public UUID getId() { return id; }
    public Long getClientId() { return clientId; }
    public UUID getRestaurantId() { return restaurantId; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    private void ensureStatusIs(OrderStatus expected, String action) {
        if (this.status != expected) {
            throw new InvalidOrderStateException(
                    "Action [" + action + "] is not allowed when status is " + status
            );
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static List<OrderItem> requireAtLeastOneItem(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        return items;
    }
}
