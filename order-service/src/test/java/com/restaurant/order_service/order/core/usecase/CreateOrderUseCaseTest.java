package com.restaurant.order_service.order.core.usecase;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.core.exception.MenuItemNotFoundException;
import com.restaurant.order_service.menu.core.exception.MenuItemUnavailableException;
import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.dto.input.CreateOrderInputDTO;
import com.restaurant.order_service.order.core.dto.input.CreateOrderItemInputDTO;
import com.restaurant.order_service.order.core.dto.output.OrderOutputDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateOrderUseCaseTest {

    private OrderGateway orderGateway;
    private MenuItemGateway menuItemGateway;
    private CreateOrderUseCase useCase;

    private static final Long CLIENT_ID = 42L;
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID BURGER_ID = UUID.randomUUID();
    private static final UUID COKE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        menuItemGateway = mock(MenuItemGateway.class);
        useCase = new CreateOrderUseCase(orderGateway, menuItemGateway);
    }

    @Test
    @DisplayName("creates order with name/price taken from catalog (not from input)")
    void createsOrderWithCatalogData() {
        MenuItem burger = MenuItem.restore(BURGER_ID, "X-Burger", "tasty", new BigDecimal("25.00"), "BURGER", true);
        MenuItem coke = MenuItem.restore(COKE_ID, "Coca-Cola", "350ml", new BigDecimal("7.00"), "DRINK", true);

        when(menuItemGateway.findById(BURGER_ID)).thenReturn(Optional.of(burger));
        when(menuItemGateway.findById(COKE_ID)).thenReturn(Optional.of(coke));
        when(orderGateway.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.assignId(UUID.randomUUID());
            return o;
        });

        CreateOrderInputDTO input = new CreateOrderInputDTO(
                CLIENT_ID, RESTAURANT_ID,
                List.of(
                        new CreateOrderItemInputDTO(BURGER_ID, 2),
                        new CreateOrderItemInputDTO(COKE_ID, 1)
                )
        );

        OrderOutputDTO output = useCase.execute(input);

        assertThat(output.id()).isNotNull();
        assertThat(output.clientId()).isEqualTo(CLIENT_ID);
        assertThat(output.status()).isEqualTo(OrderStatus.AWAITING_CONFIRMATION);
        assertThat(output.totalAmount()).isEqualByComparingTo("57.00");
        assertThat(output.items()).hasSize(2);
        assertThat(output.items().get(0).name()).isEqualTo("X-Burger");
        assertThat(output.items().get(0).unitPrice()).isEqualByComparingTo("25.00");
        assertThat(output.items().get(0).subtotal()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("throws MenuItemNotFoundException when item id does not exist in catalog")
    void throwsWhenItemMissing() {
        when(menuItemGateway.findById(BURGER_ID)).thenReturn(Optional.empty());

        CreateOrderInputDTO input = new CreateOrderInputDTO(
                CLIENT_ID, RESTAURANT_ID,
                List.of(new CreateOrderItemInputDTO(BURGER_ID, 1))
        );

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(MenuItemNotFoundException.class)
                .hasMessageContaining(BURGER_ID.toString());
    }

    @Test
    @DisplayName("throws MenuItemUnavailableException when item exists but is inactive")
    void throwsWhenItemInactive() {
        MenuItem inactive = MenuItem.restore(BURGER_ID, "X-Burger", "tasty", new BigDecimal("25.00"), "BURGER", false);
        when(menuItemGateway.findById(BURGER_ID)).thenReturn(Optional.of(inactive));

        CreateOrderInputDTO input = new CreateOrderInputDTO(
                CLIENT_ID, RESTAURANT_ID,
                List.of(new CreateOrderItemInputDTO(BURGER_ID, 1))
        );

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(MenuItemUnavailableException.class);
    }
}
