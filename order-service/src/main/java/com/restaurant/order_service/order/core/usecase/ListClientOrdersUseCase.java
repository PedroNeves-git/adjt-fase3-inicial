package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.gateway.OrderGateway;

import java.util.List;

public class ListClientOrdersUseCase {

    private final OrderGateway orderGateway;

    public ListClientOrdersUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public List<OrderOutputDTO> execute(Long clientId) {
        return orderGateway.findByClientId(clientId).stream()
                .map(OrderOutputDTO::from)
                .toList();
    }
}
