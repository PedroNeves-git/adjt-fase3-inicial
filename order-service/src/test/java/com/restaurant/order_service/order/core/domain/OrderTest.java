package com.restaurant.order_service.order.core.domain;

import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import com.restaurant.order_service.order.core.exception.InvalidOrderStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final Long CLIENT_ID = 42L;
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID MENU_ID_1 = UUID.randomUUID();
    private static final UUID MENU_ID_2 = UUID.randomUUID();

    private List<OrderItem> twoItems() {
        return List.of(
                OrderItem.create(MENU_ID_1, "X-Burger", 2, new BigDecimal("25.00")),
                OrderItem.create(MENU_ID_2, "Coca",     1, new BigDecimal("7.00"))
        );
    }

    @Test
    @DisplayName("newOrder starts in AWAITING_CONFIRMATION with computed total")
    void newOrderInitialState() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());

        assertThat(order.getId()).isNull();
        assertThat(order.getClientId()).isEqualTo(CLIENT_ID);
        assertThat(order.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.AWAITING_CONFIRMATION);
        assertThat(order.calculateTotal()).isEqualByComparingTo("57.00");
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    @DisplayName("items list is unmodifiable from outside")
    void itemsListIsUnmodifiable() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());

        assertThatThrownBy(() ->
                order.getItems().add(
                        OrderItem.create(MENU_ID_1, "X", 1, BigDecimal.ONE)
                )
        ).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("newOrder requires non-empty items list")
    void newOrderRejectsEmptyItems() {
        assertThatThrownBy(() -> Order.newOrder(CLIENT_ID, RESTAURANT_ID, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    @DisplayName("newOrder requires non-null clientId and restaurantId")
    void newOrderRejectsNullIds() {
        assertThatThrownBy(() -> Order.newOrder(null, RESTAURANT_ID, twoItems()))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> Order.newOrder(CLIENT_ID, null, twoItems()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("confirm transitions AWAITING_CONFIRMATION → PENDING_PAYMENT")
    void confirmTransitionsStatus() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        LocalDateTime beforeUpdate = order.getUpdatedAt();

        sleepTinyBit();
        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getUpdatedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("confirm fails if not in AWAITING_CONFIRMATION")
    void confirmFailsFromWrongStatus() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order.confirm();

        assertThatThrownBy(order::confirm)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("confirm");
    }

    @Test
    @DisplayName("markAsPaid transitions PENDING_PAYMENT → PAID")
    void markAsPaidTransitions() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order.confirm();

        order.markAsPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("markAsPaid fails if not in PENDING_PAYMENT")
    void markAsPaidFailsWhenNotPending() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("markAsPaid");
    }

    @Test
    @DisplayName("markAsPending only valid in PENDING_PAYMENT state (idempotent touch)")
    void markAsPendingOnlyWhenPending() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());

        assertThatThrownBy(order::markAsPending)
                .isInstanceOf(InvalidOrderStateException.class);

        order.confirm();
        order.markAsPending();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
    }

    @Test
    @DisplayName("cancel allowed from AWAITING_CONFIRMATION and PENDING_PAYMENT")
    void cancelAllowedFromPreviousStates() {
        Order order1 = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order1.cancel();
        assertThat(order1.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order order2 = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order2.confirm();
        order2.cancel();
        assertThat(order2.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel is idempotent when already cancelled")
    void cancelIdempotentWhenCancelled() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order.cancel();
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel fails if order already paid")
    void cancelFailsWhenPaid() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        order.confirm();
        order.markAsPaid();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Paid order");
    }

    @Test
    @DisplayName("belongsTo recognises owner client")
    void belongsToOwnership() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());

        assertThat(order.belongsTo(CLIENT_ID)).isTrue();
        assertThat(order.belongsTo(999L)).isFalse();
    }

    @Test
    @DisplayName("assignId works once, then fails")
    void assignIdOnceOnly() {
        Order order = Order.newOrder(CLIENT_ID, RESTAURANT_ID, twoItems());
        UUID id = UUID.randomUUID();
        order.assignId(id);

        assertThat(order.getId()).isEqualTo(id);
        assertThatThrownBy(() -> order.assignId(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("restore brings back a persisted order without touching status")
    void restorePreservesState() {
        UUID id = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        LocalDateTime updated = LocalDateTime.now().minusMinutes(30);
        OrderItem item = OrderItem.restore(
                UUID.randomUUID(), MENU_ID_1, "X", 1, new BigDecimal("10.00")
        );

        Order order = Order.restore(
                id, CLIENT_ID, RESTAURANT_ID,
                List.of(item),
                OrderStatus.PAID,
                created, updated
        );

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getCreatedAt()).isEqualTo(created);
        assertThat(order.getUpdatedAt()).isEqualTo(updated);
    }

    private static void sleepTinyBit() {
        try { Thread.sleep(2); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
