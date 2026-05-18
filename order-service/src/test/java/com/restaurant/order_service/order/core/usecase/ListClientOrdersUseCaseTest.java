package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListClientOrdersUseCaseTest {

    private OrderGateway orderGateway;
    private ListClientOrdersUseCase useCase;

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        useCase = new ListClientOrdersUseCase(orderGateway);
    }

    @Test
    @DisplayName("returns all orders for the client mapped to output DTOs")
    void mapsOrdersToDtos() {
        Long clientId = 42L;
        Order o1 = Order.newOrder(clientId, UUID.randomUUID(),
                List.of(OrderItem.create(UUID.randomUUID(), "X", 1, new BigDecimal("10.00"))));
        o1.assignId(UUID.randomUUID());
        Order o2 = Order.newOrder(clientId, UUID.randomUUID(),
                List.of(OrderItem.create(UUID.randomUUID(), "Y", 2, new BigDecimal("5.00"))));
        o2.assignId(UUID.randomUUID());

        when(orderGateway.findByClientId(clientId)).thenReturn(List.of(o1, o2));

        List<OrderOutputDTO> result = useCase.execute(clientId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).clientId()).isEqualTo(clientId);
        assertThat(result.get(1).clientId()).isEqualTo(clientId);
    }

    @Test
    @DisplayName("returns empty list when client has no orders")
    void emptyList() {
        when(orderGateway.findByClientId(99L)).thenReturn(List.of());

        assertThat(useCase.execute(99L)).isEmpty();
    }
}
