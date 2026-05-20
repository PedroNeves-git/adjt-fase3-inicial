package com.restaurant.order_service.order.infra.config;

import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import com.restaurant.order_service.order.core.gateway.OrderEventPublisher;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import com.restaurant.order_service.order.core.usecase.ConfirmOrderUseCase;
import com.restaurant.order_service.order.core.usecase.CreateOrderUseCase;
import com.restaurant.order_service.order.core.usecase.GetOrderByIdUseCase;
import com.restaurant.order_service.order.core.usecase.ListClientOrdersUseCase;
import com.restaurant.order_service.order.core.usecase.MarkOrderAsPaidUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBeanConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderGateway orderGateway,
            MenuItemGateway menuItemGateway
    ) {
        return new CreateOrderUseCase(orderGateway, menuItemGateway);
    }

    @Bean
    public ConfirmOrderUseCase confirmOrderUseCase(
            OrderGateway orderGateway,
            OrderEventPublisher eventPublisher
    ) {
        return new ConfirmOrderUseCase(orderGateway, eventPublisher);
    }

    @Bean
    public GetOrderByIdUseCase getOrderByIdUseCase(OrderGateway orderGateway) {
        return new GetOrderByIdUseCase(orderGateway);
    }

    @Bean
    public ListClientOrdersUseCase listClientOrdersUseCase(OrderGateway orderGateway) {
        return new ListClientOrdersUseCase(orderGateway);
    }

    @Bean
    public MarkOrderAsPaidUseCase markOrderAsPaidUseCase(OrderGateway orderGateway) {
        return new MarkOrderAsPaidUseCase(orderGateway);
    }
}
