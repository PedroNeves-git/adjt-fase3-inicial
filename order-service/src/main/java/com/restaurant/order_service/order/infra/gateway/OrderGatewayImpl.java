package com.restaurant.order_service.order.infra.gateway;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.gateway.OrderGateway;
import com.restaurant.order_service.order.infra.entity.OrderEntity;
import com.restaurant.order_service.order.infra.mapper.OrderEntityMapper;
import com.restaurant.order_service.order.infra.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderGatewayImpl implements OrderGateway {

    private final OrderJpaRepository repository;

    public OrderGatewayImpl(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntityMapper.toEntity(order);
        OrderEntity saved = repository.save(entity);
        return OrderEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(OrderEntityMapper::toDomain);
    }

    @Override
    public List<Order> findByClientId(Long clientId) {
        return repository.findByClientIdOrderByCreatedAtDesc(clientId).stream()
                .map(OrderEntityMapper::toDomain)
                .toList();
    }
}
