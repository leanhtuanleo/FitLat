package com.fitlat.backend.controller;

import com.fitlat.backend.dto.CreateUserRequest;
import com.fitlat.backend.entity.User;
import com.fitlat.backend.service.UserService;

import com.fitlat.backend.dto.UpdateUserRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.fitlat.backend.dto.UserResponse;
import com.fitlat.backend.mapper.UserMapper;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Lấy hồ sơ của người đang đăng nhập
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public UserResponse getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = Long.parseLong(jwt.getSubject());

        User user = userService.getUserById(userId);

        return UserMapper.toResponse(user);
    }

    // Cập nhật hồ sơ của người đang đăng nhập
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public UserResponse updateMyProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserRequest request) {

        Long userId = Long.parseLong(jwt.getSubject());

        User user = userService.updateUser(
                userId,
                request.getFullName(),
                request.getEmail()
        );

        return UserMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getFullName(),
                request.getEmail()
        );

        return UserMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userService.updateUser(
                id,
                request.getFullName(),
                request.getEmail()
        );

        return UserMapper.toResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

