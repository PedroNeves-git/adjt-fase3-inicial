package com.restaurant.order_service.menu.infra.config;

import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import com.restaurant.order_service.menu.core.usecase.ListMenuItemsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemBeanConfig {

    @Bean
    public ListMenuItemsUseCase listMenuItemsUseCase(MenuItemGateway menuItemGateway) {
        return new ListMenuItemsUseCase(menuItemGateway);
    }
}
