package com.restaurant.auth_service.infra.gateway;

import com.restaurant.auth_service.common.dto.PaginatedResponseDTO;
import com.restaurant.auth_service.core.doman.User;
import com.restaurant.auth_service.core.gateway.UserGateway;
import com.restaurant.auth_service.infra.entity.UserEntity;
import com.restaurant.auth_service.infra.mapper.UserEntityMapper;
import com.restaurant.auth_service.infra.repository.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserGatewayImpl implements UserGateway {

    private final UserJpaRepository repository;

    public UserGatewayImpl(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User create(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public User update(User user) {
        UserEntity entity = UserEntityMapper.toEntity(user);
        UserEntity saved = repository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public PaginatedResponseDTO<User> findAll(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserEntity> result = repository.findAll(pageable);

        return new PaginatedResponseDTO<>(
                result.getContent().stream().map(UserEntityMapper::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
