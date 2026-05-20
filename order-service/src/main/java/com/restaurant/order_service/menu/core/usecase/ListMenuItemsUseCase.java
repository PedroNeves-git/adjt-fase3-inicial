package com.restaurant.order_service.menu.core.usecase;

import com.restaurant.order_service.menu.core.dto.output.MenuItemOutputDTO;
import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;

import java.util.List;

public class ListMenuItemsUseCase {

    private final MenuItemGateway menuItemGateway;

    public ListMenuItemsUseCase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public List<MenuItemOutputDTO> execute() {
        return menuItemGateway.findAllActive().stream()
                .map(MenuItemOutputDTO::from)
                .toList();
    }
}
