package com.restaurant.order_service.order.infra.gateway;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.core.domain.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(OrderGatewayImpl.class)
@ActiveProfiles("test")
class OrderGatewayImplTest {

    @Autowired
    private OrderGatewayImpl gateway;

    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    private Order buildOrder(Long clientId) {
        return Order.newOrder(
                clientId,
                RESTAURANT_ID,
                List.of(
                        OrderItem.create(UUID.randomUUID(), "X-Burger", 2, new BigDecimal("25.00")),
                        OrderItem.create(UUID.randomUUID(), "Coca",     1, new BigDecimal("7.00"))
                )
        );
    }

    @Test
    @DisplayName("save assigns IDs and persists order with items")
    void saveAssignsIdsAndPersists() {
        Order saved = gateway.save(buildOrder(42L));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getItems()).hasSize(2);
        assertThat(saved.getItems()).allMatch(it -> it.getId() != null);
        assertThat(saved.calculateTotal()).isEqualByComparingTo("57.00");
    }

    @Test
    @DisplayName("findById returns the persisted order with items")
    void findByIdReturnsOrderWithItems() {
        Order saved = gateway.save(buildOrder(42L));

        Optional<Order> found = gateway.findById(saved.getId());

        assertThat(found).isPresent();
        Order reloaded = found.orElseThrow();
        assertThat(reloaded.getClientId()).isEqualTo(42L);
        assertThat(reloaded.getRestaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(reloaded.getItems()).hasSize(2);
        assertThat(reloaded.getStatus()).isEqualTo(OrderStatus.AWAITING_CONFIRMATION);
    }

    @Test
    @DisplayName("findById returns empty when id does not exist")
    void findByIdEmptyWhenMissing() {
        assertThat(gateway.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("findByClientId returns only orders of that client, newest first")
    void findByClientIdFilters() throws Exception {
        Order a1 = gateway.save(buildOrder(1L));
        Thread.sleep(5);
        Order a2 = gateway.save(buildOrder(1L));
        gateway.save(buildOrder(2L));

        List<Order> ofClient1 = gateway.findByClientId(1L);

        assertThat(ofClient1).hasSize(2);
        assertThat(ofClient1.get(0).getId()).isEqualTo(a2.getId());
        assertThat(ofClient1.get(1).getId()).isEqualTo(a1.getId());
    }

    @Test
    @DisplayName("save updates an existing order's status")
    void saveUpdatesStatus() {
        Order saved = gateway.save(buildOrder(42L));
        UUID orderId = saved.getId();

        Order reloaded = gateway.findById(orderId).orElseThrow();
        reloaded.confirm();
        gateway.save(reloaded);

        Order afterUpdate = gateway.findById(orderId).orElseThrow();
        assertThat(afterUpdate.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
    }
}
