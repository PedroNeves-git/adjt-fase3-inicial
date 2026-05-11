package com.restaurant.auth_service.core.usecase;

import com.restaurant.auth_service.core.doman.User;
import com.restaurant.auth_service.core.doman.enums.Role;
import com.restaurant.auth_service.core.doman.vo.Password;
import com.restaurant.auth_service.core.dto.input.CreateUserInputDTO;
import com.restaurant.auth_service.core.dto.input.UpdateUserInputDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.exception.EmailAlreadyInUseException;
import com.restaurant.auth_service.core.exception.UserNotFoundException;
import com.restaurant.auth_service.core.gateway.PasswordEncoderGateway;
import com.restaurant.auth_service.core.gateway.UserGateway;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UpdateUserUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoderGateway;

    public UpdateUserUseCase(
            UserGateway userGateway,
            PasswordEncoderGateway passwordEncoderGateway
    ) {
        this.userGateway = userGateway;
        this.passwordEncoderGateway = passwordEncoderGateway;
    }

    public UserOutputDTO execute(Long id, UpdateUserInputDTO input) {
        User user = userGateway.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.export().email().equals(input.email()) &&
                userGateway.existsByEmail(input.email())) {
            throw new EmailAlreadyInUseException(input.email());
        }

        String encoded = null;
        if (input.password() != null) {
            new Password(input.password());
            encoded = passwordEncoderGateway.encode(input.password());
        }

        Role role = null;
        if (input.role() != null) {
            role = Role.from(input.role());
        }

        user.update(
                input.name(),
                input.email(),
                encoded,
                role,
                input.active()
        );

        User updated = userGateway.update(user);
        return updated.toOutput();
    }
}
