package com.restaurant.order_service.order.core.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {

    private UUID id;
    private final UUID menuItemId;
    private final String name;
    private final int quantity;
    private final BigDecimal unitPrice;

    private OrderItem(UUID id, UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.menuItemId = Objects.requireNonNull(menuItemId, "menuItemId is required");
        this.name = requireName(name);
        this.quantity = requirePositiveQuantity(quantity);
        this.unitPrice = requireNonNegativePrice(unitPrice);
    }

    public static OrderItem create(UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {
        return new OrderItem(null, menuItemId, name, quantity, unitPrice);
    }

    public static OrderItem restore(UUID id, UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {
        return new OrderItem(id, menuItemId, name, quantity, unitPrice);
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void assignId(UUID id) {
        if (this.id != null) {
            throw new IllegalStateException("id already assigned");
        }
        this.id = Objects.requireNonNull(id, "id is required");
    }

    public UUID getId() { return id; }
    public UUID getMenuItemId() { return menuItemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        return name;
    }

    private static int requirePositiveQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        return quantity;
    }

    private static BigDecimal requireNonNegativePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be non-negative");
        }
        return price;
    }
}
