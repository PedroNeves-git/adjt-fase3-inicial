package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import com.restaurant.order_service.order.core.gateway.OrderGateway;

import java.util.UUID;

public class GetOrderByIdUseCase {

    private final OrderGateway orderGateway;

    public GetOrderByIdUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public OrderOutputDTO execute(UUID orderId, Long clientId) {
        Order order = orderGateway.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.belongsTo(clientId)) {
            throw new OrderAccessDeniedException();
        }

        return OrderOutputDTO.from(order);
    }
}
