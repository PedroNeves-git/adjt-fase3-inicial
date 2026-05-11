package com.restaurant.auth_service.infra.repository;

import com.restaurant.auth_service.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserJpaRepository extends JpaRepository<UserEntity,Long> {
    boolean existsByEmail(String email);
    UserDetails findByEmail(String email);

    @Modifying
    @Query("update UserEntity u set u.password = :pwd where u.id = :id")
    void updatePassword(@Param("id") Long id, @Param("pwd") String pwd);
}
