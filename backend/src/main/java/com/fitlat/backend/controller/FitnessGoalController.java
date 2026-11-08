package com.fitlat.backend.controller;

import com.fitlat.backend.dto.profile.*;
import com.fitlat.backend.service.FitnessGoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/goals")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class FitnessGoalController {

    private final FitnessGoalService goalService;

    public FitnessGoalController(FitnessGoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public List<FitnessGoalResponse> getMyGoals(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return goalService.getMyGoals(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FitnessGoalResponse createGoal(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateFitnessGoalRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return goalService.createGoal(userId, request);
    }

    @PutMapping("/{id}")
    public FitnessGoalResponse updateGoal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @Valid @RequestBody UpdateFitnessGoalRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());

        return goalService.updateGoal(userId, id, request);
    }
}