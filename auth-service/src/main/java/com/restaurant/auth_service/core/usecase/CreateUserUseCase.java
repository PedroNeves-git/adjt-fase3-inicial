package com.restaurant.auth_service.core.usecase;

import com.restaurant.auth_service.core.doman.User;
import com.restaurant.auth_service.core.doman.enums.Role;
import com.restaurant.auth_service.core.doman.vo.Password;
import com.restaurant.auth_service.core.dto.input.CreateUserInputDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.exception.EmailAlreadyInUseException;
import com.restaurant.auth_service.core.gateway.PasswordEncoderGateway;
import com.restaurant.auth_service.core.gateway.UserGateway;

public class CreateUserUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public CreateUserUseCase(
            UserGateway userGateway,
            PasswordEncoderGateway passwordEncoderGateway
    ) {
        this.userGateway = userGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
    }

    public UserOutputDTO execute(CreateUserInputDTO input) {
        if (userGateway.existsByEmail(input.email())) {
            throw new EmailAlreadyInUseException(input.email());
        }

        new Password(input.password());
        String encoded = passwordEncoderGateway.encode(input.password());

        Role role = Role.from(input.role());

        User user = User.newUser(
                input.name(),
                input.email(),
                encoded,
                role
        );

        User createdUser = userGateway.create(user);
        return createdUser.toOutput();
    }
}
