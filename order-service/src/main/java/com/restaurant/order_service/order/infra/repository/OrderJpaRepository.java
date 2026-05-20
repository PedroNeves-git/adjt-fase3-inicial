package com.restaurant.order_service.order.infra.repository;

import com.restaurant.order_service.order.infra.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByClientIdOrderByCreatedAtDesc(Long clientId);
}
