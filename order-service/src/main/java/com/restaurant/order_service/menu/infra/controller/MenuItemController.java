package com.restaurant.order_service.menu.infra.controller;

import com.restaurant.order_service.menu.core.dto.output.MenuItemOutputDTO;
import com.restaurant.order_service.menu.core.usecase.ListMenuItemsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu-items")
@Tag(name = "Menu", description = "Available items the customer can pick when building an order")
public class MenuItemController {

    private final ListMenuItemsUseCase listMenuItems;

    public MenuItemController(ListMenuItemsUseCase listMenuItems) {
        this.listMenuItems = listMenuItems;
    }

    @GetMapping
    @Operation(summary = "List all active items in the catalog")
    public List<MenuItemOutputDTO> list() {
        return listMenuItems.execute();
    }
}
