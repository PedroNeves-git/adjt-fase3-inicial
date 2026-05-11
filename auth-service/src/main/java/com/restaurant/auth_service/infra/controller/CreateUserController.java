package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.core.dto.input.CreateUserInputDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.usecase.CreateUserUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users")
@RestController
@RequestMapping("/users")
public class CreateUserController {
    private final CreateUserUseCase createUserUseCase;

    public CreateUserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserOutputDTO create(@RequestBody CreateUserInputDTO request) {
        return createUserUseCase.execute(request);
    }
}
