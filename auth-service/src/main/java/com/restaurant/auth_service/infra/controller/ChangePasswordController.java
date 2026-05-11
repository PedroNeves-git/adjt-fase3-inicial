package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.infra.dto.ChangePasswordRequest;
import com.restaurant.auth_service.infra.security.ChangePasswordService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    public ChangePasswordController(ChangePasswordService changePasswordService) {
        this.changePasswordService = changePasswordService;
    }

    @PutMapping("/me/password")
    public void changePassword(@RequestBody ChangePasswordRequest req) {
        changePasswordService.changePassword(req);
    }
}
