package com.restaurant.auth_service.core.usecase;

import com.restaurant.auth_service.core.exception.UserNotFoundException;
import com.restaurant.auth_service.core.gateway.UserGateway;

public class DeleteUserUseCase {
    private UserGateway userGateway;

    public DeleteUserUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public void execute(Long id) {
        userGateway.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userGateway.deleteById(id);
    }
}
