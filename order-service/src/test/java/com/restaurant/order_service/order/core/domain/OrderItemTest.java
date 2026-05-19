package com.restaurant.order_service.order.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    private static final UUID MENU_ID = UUID.randomUUID();

    @Test
    @DisplayName("calculateSubtotal multiplies unit price by quantity")
    void calculateSubtotal() {
        OrderItem item = OrderItem.create(MENU_ID, "X-Burger", 3, new BigDecimal("25.00"));

        assertThat(item.calculateSubtotal()).isEqualByComparingTo("75.00");
    }

    @Test
    @DisplayName("create rejects null menuItemId")
    void rejectsNullMenuItemId() {
        assertThatThrownBy(() -> OrderItem.create(null, "X", 1, BigDecimal.ONE))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("create rejects blank name")
    void rejectsBlankName() {
        assertThatThrownBy(() -> OrderItem.create(MENU_ID, " ", 1, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Name");
    }

    @Test
    @DisplayName("create rejects quantity zero or negative")
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> OrderItem.create(MENU_ID, "X", 0, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity");

        assertThatThrownBy(() -> OrderItem.create(MENU_ID, "X", -1, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("create rejects negative price")
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> OrderItem.create(MENU_ID, "X", 1, new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unit price");
    }

    @Test
    @DisplayName("create allows zero price (e.g. promotional item)")
    void allowsZeroPrice() {
        OrderItem item = OrderItem.create(MENU_ID, "Brinde", 1, BigDecimal.ZERO);

        assertThat(item.calculateSubtotal()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("assignId works once, then fails")
    void assignIdOnceOnly() {
        OrderItem item = OrderItem.create(MENU_ID, "X", 1, BigDecimal.ONE);
        UUID id = UUID.randomUUID();

        item.assignId(id);
        assertThat(item.getId()).isEqualTo(id);

        assertThatThrownBy(() -> item.assignId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("restore preserves all fields including id")
    void restorePreservesFields() {
        UUID id = UUID.randomUUID();
        OrderItem item = OrderItem.restore(id, MENU_ID, "Coca", 2, new BigDecimal("7.00"));

        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getMenuItemId()).isEqualTo(MENU_ID);
        assertThat(item.getName()).isEqualTo("Coca");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("7.00");
    }
}
