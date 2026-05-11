package com.restaurant.auth_service.infra.security;

import com.restaurant.auth_service.infra.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Authz {
    public boolean isSelf(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) auth.getPrincipal();
        return user.getId().equals(id);
    }
}