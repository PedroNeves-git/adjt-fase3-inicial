package com.restaurant.auth_service.core.usecase;

import com.restaurant.auth_service.common.dto.PaginatedResponseDTO;
import com.restaurant.auth_service.core.doman.User;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.gateway.UserGateway;

import java.util.List;

public class ListUsersUseCase {
    private UserGateway userGateway;

    public ListUsersUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public PaginatedResponseDTO<UserOutputDTO> execute(int page, int size) {
        PaginatedResponseDTO<User> users = userGateway.findAll(page, size);

        List<UserOutputDTO> content = users.content().stream()
                .map(User::toOutput)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                users.page(),
                users.size(),
                users.totalElements(),
                users.totalPages()
        );
    }
}
