package com.restaurant.order_service.order.core.gateway;

import com.restaurant.order_service.order.core.domain.Order;

public interface OrderEventPublisher {

    void publishOrderCreated(Order order);
}
