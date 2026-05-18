package com.restaurant.order_service.menu.infra.gateway;

import com.restaurant.order_service.menu.core.domain.MenuItem;
import com.restaurant.order_service.menu.core.gateway.MenuItemGateway;
import com.restaurant.order_service.menu.infra.mapper.MenuItemEntityMapper;
import com.restaurant.order_service.menu.infra.repository.MenuItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MenuItemGatewayImpl implements MenuItemGateway {

    private final MenuItemJpaRepository repository;

    public MenuItemGatewayImpl(MenuItemJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MenuItem> findById(UUID id) {
        return repository.findById(id).map(MenuItemEntityMapper::toDomain);
    }

    @Override
    public List<MenuItem> findAllActive() {
        return repository.findAllByActiveTrueOrderByCategoryAscNameAsc().stream()
                .map(MenuItemEntityMapper::toDomain)
                .toList();
    }
}
