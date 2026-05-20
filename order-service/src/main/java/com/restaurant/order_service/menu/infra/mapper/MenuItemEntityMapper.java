package com.restaurant.order_service.menu.infra.mapper;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.infra.entity.MenuItemEntity;

public final class MenuItemEntityMapper {

    private MenuItemEntityMapper() { }

    public static MenuItem toDomain(MenuItemEntity entity) {
        return MenuItem.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.isActive()
        );
    }
}
