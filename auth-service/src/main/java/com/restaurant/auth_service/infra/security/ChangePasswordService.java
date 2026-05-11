package com.restaurant.auth_service.infra.security;

import com.restaurant.auth_service.core.doman.vo.Password;
import com.restaurant.auth_service.infra.dto.ChangePasswordRequest;
import com.restaurant.auth_service.infra.entity.UserEntity;
import com.restaurant.auth_service.infra.repository.UserJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;

    public ChangePasswordService(
            PasswordEncoder passwordEncoder,
            UserJpaRepository userJpaRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userJpaRepository = userJpaRepository;
    }

    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        if (!req.newPassword().equals(req.confirmNewPassword())) {
            throw new RuntimeException("Nova senha não confere");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) auth.getPrincipal();

        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new RuntimeException("Senha atual inválida");
        }

        new Password(req.newPassword());

        userJpaRepository.updatePassword(user.getId(), passwordEncoder.encode(req.newPassword()));
    }
}
