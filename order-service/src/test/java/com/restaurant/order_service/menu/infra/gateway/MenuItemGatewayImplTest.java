package com.restaurant.order_service.menu.infra.gateway;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.infra.entity.MenuItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MenuItemGatewayImpl.class)
@ActiveProfiles("test")
class MenuItemGatewayImplTest {

    @Autowired
    private MenuItemGatewayImpl gateway;

    @Autowired
    private TestEntityManager em;

    private MenuItemEntity persistItem(String name, String category, boolean active) {
        MenuItemEntity entity = new MenuItemEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(name);
        entity.setDescription("desc");
        entity.setPrice(new BigDecimal("10.00"));
        entity.setCategory(category);
        entity.setActive(active);
        em.persist(entity);
        return entity;
    }

    @Test
    @DisplayName("findAllActive returns only active items, ordered by category then name")
    void findAllActiveFiltersAndOrders() {
        persistItem("X-Bacon",        "BURGER", true);
        persistItem("X-Burger",       "BURGER", true);
        persistItem("Old Promo",      "BURGER", false);
        persistItem("Coca-Cola",      "DRINK",  true);
        em.flush();

        List<MenuItem> result = gateway.findAllActive();

        assertThat(result).hasSize(3);
        assertThat(result).extracting(MenuItem::getName)
                .containsExactly("X-Bacon", "X-Burger", "Coca-Cola");
    }

    @Test
    @DisplayName("findById returns the item when present")
    void findByIdReturnsItem() {
        MenuItemEntity persisted = persistItem("X-Burger", "BURGER", true);
        em.flush();

        Optional<MenuItem> found = gateway.findById(persisted.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("X-Burger");
        assertThat(found.get().isAvailable()).isTrue();
    }

    @Test
    @DisplayName("findById returns empty when id does not exist")
    void findByIdEmptyWhenMissing() {
        assertThat(gateway.findById(UUID.randomUUID())).isEmpty();
    }
}
