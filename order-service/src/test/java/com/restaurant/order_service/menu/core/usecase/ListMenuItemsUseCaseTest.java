package com.restaurant.order_service.menu.core.usecase;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.core.dto.output.MenuItemOutputDTO;
import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListMenuItemsUseCaseTest {

    private MenuItemGateway gateway;
    private ListMenuItemsUseCase useCase;

    @BeforeEach
    void setUp() {
        gateway = mock(MenuItemGateway.class);
        useCase = new ListMenuItemsUseCase(gateway);
    }

    @Test
    @DisplayName("maps active items to output DTOs")
    void mapsToDtos() {
        MenuItem burger = MenuItem.restore(
                UUID.randomUUID(), "X-Burger", "tasty",
                new BigDecimal("25.00"), "BURGER", true
        );
        MenuItem coke = MenuItem.restore(
                UUID.randomUUID(), "Coca", "350ml",
                new BigDecimal("7.00"), "DRINK", true
        );
        when(gateway.findAllActive()).thenReturn(List.of(burger, coke));

        List<MenuItemOutputDTO> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("X-Burger");
        assertThat(result.get(0).price()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("returns empty list when catalog is empty")
    void emptyList() {
        when(gateway.findAllActive()).thenReturn(List.of());

        assertThat(useCase.execute()).isEmpty();
    }
}
