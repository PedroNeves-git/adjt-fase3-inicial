package com.restaurant.auth_service.core.usecase;

import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.exception.UserNotFoundException;
import com.restaurant.auth_service.core.gateway.UserGateway;

public class ListUserByIdUseCase {
    private UserGateway userGateway;

    public ListUserByIdUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public UserOutputDTO execute(Long id) {
        return userGateway.findById(id)
                .map(user -> user.toOutput())
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
