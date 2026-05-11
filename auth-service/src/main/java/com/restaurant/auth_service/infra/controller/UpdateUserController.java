package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.core.dto.input.UpdateUserInputDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.usecase.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class UpdateUserController {
    private final UpdateUserUseCase updateUserUseCase;

    public UpdateUserController(UpdateUserUseCase updateUserUseCase) {
        this.updateUserUseCase = updateUserUseCase;
    }

    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id)")
    @PutMapping("/{id}")
    public UserOutputDTO update(
            @PathVariable Long id,
            @RequestBody UpdateUserInputDTO input
    ) {
        return updateUserUseCase.execute(id, input);
    }
}
