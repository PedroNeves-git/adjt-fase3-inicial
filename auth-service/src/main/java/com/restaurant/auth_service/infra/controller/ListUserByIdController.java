package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.usecase.ListUserByIdUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class ListUserByIdController {
    private final ListUserByIdUseCase usersUseCase;

    public ListUserByIdController(ListUserByIdUseCase usersUseCase) {
        this.usersUseCase = usersUseCase;
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id)")
    @GetMapping("/{id}")
    public UserOutputDTO listUserById(
            @PathVariable Long id
    ) {
        return usersUseCase.execute(id);
    }
}
