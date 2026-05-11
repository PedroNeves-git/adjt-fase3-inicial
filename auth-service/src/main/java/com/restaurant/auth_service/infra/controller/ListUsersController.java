package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.common.dto.PaginatedResponseDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.usecase.ListUsersUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class ListUsersController {
    private final ListUsersUseCase usersUseCase;

    public ListUsersController(ListUsersUseCase usersUseCase) {
        this.usersUseCase = usersUseCase;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PaginatedResponseDTO<UserOutputDTO> execute(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return usersUseCase.execute(page, size);
    }
}





