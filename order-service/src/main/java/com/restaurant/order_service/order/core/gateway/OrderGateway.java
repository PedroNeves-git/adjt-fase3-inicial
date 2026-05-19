package com.restaurant.order_service.order.core.gateway;

import com.restaurant.order_service.order.core.domain.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderGateway {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByClientId(Long clientId);
}
