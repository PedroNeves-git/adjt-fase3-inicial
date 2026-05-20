package com.restaurant.order_service.menu.infra.repository;

import com.restaurant.order_service.menu.infra.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemJpaRepository extends JpaRepository<MenuItemEntity, UUID> {

    List<MenuItemEntity> findAllByActiveTrueOrderByCategoryAscNameAsc();
}
