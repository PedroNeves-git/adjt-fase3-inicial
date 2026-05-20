package com.restaurant.order_service.menu.core.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MenuItem {

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String category;
    private final boolean active;

    private MenuItem(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            String category,
            boolean active
    ) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = requireName(name);
        this.description = description;
        this.price = requireNonNegativePrice(price);
        this.category = category;
        this.active = active;
    }

    public static MenuItem restore(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            String category,
            boolean active
    ) {
        return new MenuItem(id, name, description, price, category, active);
    }

    public boolean isAvailable() {
        return active;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        return name;
    }

    private static BigDecimal requireNonNegativePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        return price;
    }
}
