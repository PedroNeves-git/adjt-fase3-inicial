package com.restaurant.order_service.menu.core.exception;

import java.util.UUID;

public class MenuItemUnavailableException extends RuntimeException {
    public MenuItemUnavailableException(UUID id) {
        super("Menu item is not available: " + id);
    }
}
