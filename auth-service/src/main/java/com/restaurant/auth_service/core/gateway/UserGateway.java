package com.restaurant.auth_service.core.gateway;

import com.restaurant.auth_service.common.dto.PaginatedResponseDTO;
import com.restaurant.auth_service.core.doman.User;

import java.util.Optional;

public interface UserGateway {
    User create(User user);
    User update(User user);
    Optional<User> findById(Long id);
    void deleteById(Long id);

    boolean existsByEmail(String email);

    PaginatedResponseDTO<User> findAll(int page, int size);
}
