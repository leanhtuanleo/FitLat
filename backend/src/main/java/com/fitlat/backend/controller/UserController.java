package com.fitlat.backend.controller;

import com.fitlat.backend.dto.CreateUserRequest;
import com.fitlat.backend.entity.User;
import com.fitlat.backend.service.UserService;

import com.fitlat.backend.dto.UpdateUserRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fitlat.backend.dto.UserResponse;
import com.fitlat.backend.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(
                request.getFullName(),
                request.getEmail()
        );

        return UserMapper.toResponse(user);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toResponse(user);
    }

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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}

