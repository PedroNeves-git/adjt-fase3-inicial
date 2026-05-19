package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MarkOrderAsPaidUseCaseTest {

    private OrderGateway orderGateway;
    private MarkOrderAsPaidUseCase useCase;

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        useCase = new MarkOrderAsPaidUseCase(orderGateway);
    }

    private Order pendingOrder(UUID id) {
        OrderItem item = OrderItem.restore(UUID.randomUUID(), UUID.randomUUID(), "X", 1, new BigDecimal("10.00"));
        Order order = Order.restore(
                id, 42L, UUID.randomUUID(), List.of(item),
                OrderStatus.PENDING_PAYMENT,
                LocalDateTime.now().minusMinutes(5), LocalDateTime.now().minusMinutes(5)
        );
        return order;
    }

    @Test
    @DisplayName("marks PENDING_PAYMENT order as PAID and saves")
    void transitionsToPaid() {
        UUID id = UUID.randomUUID();
        Order order = pendingOrder(id);
        when(orderGateway.findById(id)).thenReturn(Optional.of(order));

        useCase.execute(id);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderGateway).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("does nothing when order is unknown (idempotent on missing)")
    void unknownOrder() {
        UUID id = UUID.randomUUID();
        when(orderGateway.findById(id)).thenReturn(Optional.empty());

        useCase.execute(id);

        verify(orderGateway, never()).save(any());
    }

    @Test
    @DisplayName("is idempotent when order is already PAID")
    void alreadyPaid() {
        UUID id = UUID.randomUUID();
        OrderItem item = OrderItem.restore(UUID.randomUUID(), UUID.randomUUID(), "X", 1, new BigDecimal("10.00"));
        Order paid = Order.restore(
                id, 42L, UUID.randomUUID(), List.of(item),
                OrderStatus.PAID,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(orderGateway.findById(id)).thenReturn(Optional.of(paid));

        useCase.execute(id);

        verify(orderGateway, never()).save(any());
    }

    @Test
    @DisplayName("does nothing if order is in another state (e.g. CANCELLED)")
    void cancelledOrder() {
        UUID id = UUID.randomUUID();
        OrderItem item = OrderItem.restore(UUID.randomUUID(), UUID.randomUUID(), "X", 1, new BigDecimal("10.00"));
        Order cancelled = Order.restore(
                id, 42L, UUID.randomUUID(), List.of(item),
                OrderStatus.CANCELLED,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(orderGateway.findById(id)).thenReturn(Optional.of(cancelled));

        useCase.execute(id);

        verify(orderGateway, never()).save(any());
    }
}
