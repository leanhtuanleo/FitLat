package com.fitlat.backend.controller;

import com.fitlat.backend.dto.RegisterRequest;
import com.fitlat.backend.dto.UserResponse;
import com.fitlat.backend.entity.User;
import com.fitlat.backend.mapper.UserMapper;

import jakarta.validation.Valid;

import com.fitlat.backend.dto.LoginRequest;
import com.fitlat.backend.dto.LoginResponse;
import com.fitlat.backend.service.AuthService;

import com.fitlat.backend.dto.RefreshTokenRequest;
import com.fitlat.backend.dto.RefreshTokenResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService; 
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request);
        UserResponse response = UserMapper.toResponse(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response = authService.refresh(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request);

        return ResponseEntity.noContent().build();
    }
}
