package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.exception.OrderAccessDeniedException;
import com.restaurant.order_service.order.core.exception.OrderNotFoundException;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetOrderByIdUseCaseTest {

    private OrderGateway orderGateway;
    private GetOrderByIdUseCase useCase;

    private static final Long CLIENT_ID = 42L;

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        useCase = new GetOrderByIdUseCase(orderGateway);
    }

    private Order buildOrder(UUID id, Long clientId) {
        Order order = Order.newOrder(
                clientId,
                UUID.randomUUID(),
                List.of(OrderItem.create(UUID.randomUUID(), "X", 1, new BigDecimal("10.00")))
        );
        order.assignId(id);
        return order;
    }

    @Test
    @DisplayName("returns order DTO when found and owned by client")
    void happyPath() {
        UUID id = UUID.randomUUID();
        when(orderGateway.findById(id)).thenReturn(Optional.of(buildOrder(id, CLIENT_ID)));

        OrderOutputDTO output = useCase.execute(id, CLIENT_ID);

        assertThat(output.id()).isEqualTo(id);
        assertThat(output.clientId()).isEqualTo(CLIENT_ID);
    }

    @Test
    @DisplayName("throws OrderNotFoundException when missing")
    void notFound() {
        UUID id = UUID.randomUUID();
        when(orderGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, CLIENT_ID))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    @DisplayName("throws OrderAccessDeniedException when not owner")
    void accessDenied() {
        UUID id = UUID.randomUUID();
        when(orderGateway.findById(id)).thenReturn(Optional.of(buildOrder(id, CLIENT_ID)));

        assertThatThrownBy(() -> useCase.execute(id, 999L))
                .isInstanceOf(OrderAccessDeniedException.class);
    }
}
