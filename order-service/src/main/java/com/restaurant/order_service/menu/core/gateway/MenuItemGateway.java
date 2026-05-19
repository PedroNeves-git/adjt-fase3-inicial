package com.restaurant.order_service.menu.core.gateway;

import com.restaurant.order_service.menu.core.domain.MenuItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemGateway {

    Optional<MenuItem> findById(UUID id);

    List<MenuItem> findAllActive();
}
