package com.restaurant.auth_service.infra.mapper;

import com.restaurant.auth_service.core.doman.User;
import com.restaurant.auth_service.infra.entity.UserEntity;

public class UserEntityMapper {

    public static UserEntity toEntity(User user) {
        var data = user.export();

        return new UserEntity(
                data.id(),
                data.name(),
                data.email(),
                data.password(),
                data.active(),
                data.role(),
                data.createdAt(),
                data.updatedAt()
        );
    }

    public static User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getActive(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
