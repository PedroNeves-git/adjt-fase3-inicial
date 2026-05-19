package com.restaurant.auth_service.infra.controller;

import com.restaurant.auth_service.infra.dto.LoginRequest;
import com.restaurant.auth_service.infra.dto.LoginResponse;
import com.restaurant.auth_service.infra.security.JwtService;
import com.restaurant.auth_service.infra.security.exception.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            String token = jwtService.generateToken(
                    auth.getName()
            );

            return new LoginResponse(
                    token,
                    "Bearer",
                    jwtService.getExpiresInSeconds()
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
