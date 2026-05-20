package com.restaurant.order_service.order.infra.mapper;

import com.restaurant.order_service.order.core.domain.Order;
import com.restaurant.order_service.order.core.domain.OrderItem;
import com.restaurant.order_service.order.infra.entity.OrderEntity;
import com.restaurant.order_service.order.infra.entity.OrderItemEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OrderEntityMapper {

    private OrderEntityMapper() { }

    public static OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId() != null ? order.getId() : UUID.randomUUID());
        entity.setClientId(order.getClientId());
        entity.setRestaurantId(order.getRestaurantId());
        entity.setTotalAmount(order.calculateTotal());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setId(item.getId() != null ? item.getId() : UUID.randomUUID());
            itemEntity.setOrder(entity);
            itemEntity.setMenuItemId(item.getMenuItemId());
            itemEntity.setName(item.getName());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setUnitPrice(item.getUnitPrice());
            itemEntities.add(itemEntity);
        }
        entity.setItems(itemEntities);
        return entity;
    }

    public static Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(ie -> OrderItem.restore(
                        ie.getId(),
                        ie.getMenuItemId(),
                        ie.getName(),
                        ie.getQuantity(),
                        ie.getUnitPrice()
                ))
                .toList();

        return Order.restore(
                entity.getId(),
                entity.getClientId(),
                entity.getRestaurantId(),
                items,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
