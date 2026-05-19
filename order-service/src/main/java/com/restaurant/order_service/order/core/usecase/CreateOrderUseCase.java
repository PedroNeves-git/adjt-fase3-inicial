package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.core.exception.MenuItemNotFoundException;
import com.restaurant.order_service.menu.core.exception.MenuItemUnavailableException;
import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.dto.input.CreateOrderInputDTO;
import com.restaurant.order_service.order.core.dto.input.CreateOrderItemInputDTO;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.gateway.OrderGateway;

import java.util.List;

public class CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final MenuItemGateway menuItemGateway;

    public CreateOrderUseCase(OrderGateway orderGateway, MenuItemGateway menuItemGateway) {
        this.orderGateway = orderGateway;
        this.menuItemGateway = menuItemGateway;
    }

    public OrderOutputDTO execute(CreateOrderInputDTO input) {
        List<OrderItem> items = input.items().stream()
                .map(this::resolveItem)
                .toList();

        Order order = Order.newOrder(input.clientId(), input.restaurantId(), items);
        Order saved = orderGateway.save(order);
        return OrderOutputDTO.from(saved);
    }

    private OrderItem resolveItem(CreateOrderItemInputDTO requested) {
        MenuItem menuItem = menuItemGateway.findById(requested.menuItemId())
                .orElseThrow(() -> new MenuItemNotFoundException(requested.menuItemId()));

        if (!menuItem.isAvailable()) {
            throw new MenuItemUnavailableException(menuItem.getId());
        }

        return OrderItem.create(
                menuItem.getId(),
                menuItem.getName(),
                requested.quantity(),
                menuItem.getPrice()
        );
    }
}
