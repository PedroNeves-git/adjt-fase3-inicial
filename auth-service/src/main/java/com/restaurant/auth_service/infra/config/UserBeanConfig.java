package com.restaurant.auth_service.infra.config;

import com.restaurant.auth_service.core.dto.output.UserOutputDTO;
import com.restaurant.auth_service.core.gateway.PasswordEncoderGateway;
import com.restaurant.auth_service.core.gateway.UserGateway;
import com.restaurant.auth_service.core.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserBeanConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(
            UserGateway userGateway,
            PasswordEncoderGateway passwordEncoderGateway
    ) {
        return new CreateUserUseCase(userGateway, passwordEncoderGateway);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(
            UserGateway userGateway
    ) {
        return new DeleteUserUseCase(userGateway);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserGateway userGateway) {
        return new ListUsersUseCase(userGateway);
    }

    @Bean
    public ListUserByIdUseCase listUserByIdUseCase(UserGateway userGateway) {
        return new ListUserByIdUseCase(userGateway);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserGateway userGateway, PasswordEncoderGateway passwordEncoderGateway) {
        return new UpdateUserUseCase(userGateway, passwordEncoderGateway);
    }
}
