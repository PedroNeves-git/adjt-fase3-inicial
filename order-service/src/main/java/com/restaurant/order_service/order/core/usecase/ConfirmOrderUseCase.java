package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import com.restaurant.order_service.order.core.gateway.OrderEventPublisher;
import com.restaurant.order_service.order.core.gateway.OrderGateway;

import java.util.UUID;

public class ConfirmOrderUseCase {

    private final OrderGateway orderGateway;
    private final OrderEventPublisher eventPublisher;

    public ConfirmOrderUseCase(OrderGateway orderGateway, OrderEventPublisher eventPublisher) {
        this.orderGateway = orderGateway;
        this.eventPublisher = eventPublisher;
    }

    public OrderOutputDTO execute(UUID orderId, Long clientId) {
        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.belongsTo(clientId)) {
            throw new OrderAccessDeniedException();
        }

        order.confirm();
        Order saved = orderGateway.save(order);

        eventPublisher.publishOrderCreated(saved);

        return OrderOutputDTO.from(saved);
    }
}
