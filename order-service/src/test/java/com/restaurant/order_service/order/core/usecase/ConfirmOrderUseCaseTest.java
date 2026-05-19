package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import com.restaurant.order_service.order.core.gateway.OrderEventPublisher;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfirmOrderUseCaseTest {

    private OrderGateway orderGateway;
    private OrderEventPublisher eventPublisher;
    private ConfirmOrderUseCase useCase;

    private static final Long CLIENT_ID = 42L;
    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        eventPublisher = mock(OrderEventPublisher.class);
        useCase = new ConfirmOrderUseCase(orderGateway, eventPublisher);
    }

    private Order buildAwaitingOrder(UUID id, Long clientId) {
        Order order = Order.newOrder(
                clientId,
                RESTAURANT_ID,
                List.of(OrderItem.create(UUID.randomUUID(), "X", 1, new BigDecimal("10.00")))
        );
        order.assignId(id);
        return order;
    }

    @Test
    @DisplayName("transitions to PENDING_PAYMENT, saves and publishes event")
    void happyPath() {
        UUID orderId = UUID.randomUUID();
        Order order = buildAwaitingOrder(orderId, CLIENT_ID);

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(orderGateway.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderOutputDTO output = useCase.execute(orderId, CLIENT_ID);

        assertThat(output.status()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(eventPublisher).publishOrderCreated(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
    }

    @Test
    @DisplayName("throws OrderNotFoundException when order does not exist")
    void notFound() {
        UUID orderId = UUID.randomUUID();
        when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(orderId, CLIENT_ID))
                .isInstanceOf(OrderNotFoundException.class);

        verify(eventPublisher, never()).publishOrderCreated(any());
    }

    @Test
    @DisplayName("throws OrderAccessDeniedException when client does not own the order")
    void accessDenied() {
        UUID orderId = UUID.randomUUID();
        Order order = buildAwaitingOrder(orderId, CLIENT_ID);
        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> useCase.execute(orderId, 999L))
                .isInstanceOf(OrderAccessDeniedException.class);

        verify(eventPublisher, never()).publishOrderCreated(any());
    }
}
