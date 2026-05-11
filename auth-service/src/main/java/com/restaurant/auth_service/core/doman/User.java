package com.restaurant.auth_service.core.doman;

import com.restaurant.auth_service.core.doman.enums.Role;
import com.restaurant.auth_service.core.doman.vo.Email;
import com.restaurant.auth_service.core.doman.vo.Password;
import com.restaurant.auth_service.core.dto.UserPersistenceDTO;
import com.restaurant.auth_service.core.dto.output.UserOutputDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private Long id;
    private String name;
    private Email email;
    private Password password;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User (
            Long id,
            String name,
            Email email,
            Password password,
            Role role,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = requireName(name);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.role = Objects.requireNonNull(role);
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User newUser (
            String name,
            String email,
            String password,
            Role role
    ) {
        return new User(
                null,
                name,
                new Email(email),
                new Password(password),
                role,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public UserOutputDTO toOutput() {
        return new UserOutputDTO(id, name, email.value(), role, active, createdAt, updatedAt);
    }

    public static User restore(
            Long id,
            String name,
            String email,
            String password,
            Boolean active,
            Role role,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new User(
                id,
                name,
                new Email(email),
                new Password(password),
                role,
                Boolean.TRUE.equals(active),
                createdAt,
                updatedAt
        );
    }

    public UserPersistenceDTO export() {
        return new UserPersistenceDTO(id, name, email.value(), password.value(), role, active, createdAt, updatedAt);
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        return name;
    }

    public void update(
            String name,
            String email,
            String password,
            Role role,
            Boolean active
    ) {
        if (name != null) this.name = requireName(name);
        if (email != null) this.email = new Email(email);
        if (password != null) this.password = new Password(password);
        if (role != null) this.role = role;
        if (active != null) this.active = active;
        refreshUpdatedAt();
    }

    private void refreshUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
