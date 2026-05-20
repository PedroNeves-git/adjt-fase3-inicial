package com.restaurant.order_service.menu.core.dto.output;

import com.restaurant.order_service.menu.core.domain.MenuItem;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemOutputDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        boolean active
) {
    public static MenuItemOutputDTO from(MenuItem item) {
        return new MenuItemOutputDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.isActive()
        );
    }
}
