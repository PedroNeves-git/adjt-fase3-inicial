package com.restaurant.order_service.menu.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuItemTest {

    private static final UUID ID = UUID.randomUUID();

    @Test
    @DisplayName("restore preserves all fields")
    void restorePreservesFields() {
        MenuItem item = MenuItem.restore(
                ID, "X-Burger", "tasty", new BigDecimal("25.00"), "BURGER", true
        );

        assertThat(item.getId()).isEqualTo(ID);
        assertThat(item.getName()).isEqualTo("X-Burger");
        assertThat(item.getDescription()).isEqualTo("tasty");
        assertThat(item.getPrice()).isEqualByComparingTo("25.00");
        assertThat(item.getCategory()).isEqualTo("BURGER");
        assertThat(item.isActive()).isTrue();
        assertThat(item.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("inactive item is not available")
    void inactiveIsNotAvailable() {
        MenuItem item = MenuItem.restore(
                ID, "X", "d", new BigDecimal("1"), "BURGER", false
        );

        assertThat(item.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("rejects blank name")
    void rejectsBlankName() {
        assertThatThrownBy(() -> MenuItem.restore(
                ID, "  ", "d", new BigDecimal("1"), "BURGER", true
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects negative price")
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> MenuItem.restore(
                ID, "X", "d", new BigDecimal("-0.01"), "BURGER", true
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects null id")
    void rejectsNullId() {
        assertThatThrownBy(() -> MenuItem.restore(
                null, "X", "d", new BigDecimal("1"), "BURGER", true
        )).isInstanceOf(NullPointerException.class);
    }
}
