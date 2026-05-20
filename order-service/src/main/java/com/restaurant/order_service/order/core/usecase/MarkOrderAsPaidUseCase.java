package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class MarkOrderAsPaidUseCase {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaidUseCase.class);

    private final OrderGateway orderGateway;

    public MarkOrderAsPaidUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public void execute(UUID orderId) {
        Optional<Order> maybeOrder = orderGateway.findById(orderId);
        if (maybeOrder.isEmpty()) {
            log.warn("Received payment-approved event for unknown order {}", orderId);
            return;
        }

        Order order = maybeOrder.get();

        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Order {} already PAID — ignoring duplicate event", orderId);
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.warn(
                    "Cannot mark order {} as paid from current status {} — ignoring",
                    orderId, order.getStatus()
            );
            return;
        }

        order.markAsPaid();
        orderGateway.save(order);
        log.info("Order {} marked as PAID", orderId);
    }
}
