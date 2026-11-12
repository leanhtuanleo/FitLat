package com.fitlat.backend.controller;

import com.fitlat.backend.dto.workout.*;
import com.fitlat.backend.service.UserWorkoutPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/workout-plans")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class UserWorkoutPlanController {

    private final UserWorkoutPlanService planService;

    public UserWorkoutPlanController(UserWorkoutPlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public List<UserWorkoutPlanResponse> getAll() {
        return planService.getAll(currentUserId());
    }

    @GetMapping("/{planId}")
    public UserWorkoutPlanResponse getById(
            @PathVariable Long planId) {
        return planService.getById(currentUserId(), planId);
    }

    @PostMapping
    public ResponseEntity<UserWorkoutPlanResponse> create(
            @Valid @RequestBody CreateWorkoutPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.create(currentUserId(), request));
    }

    @PostMapping("/from-programs/{programId}")
    public ResponseEntity<UserWorkoutPlanResponse> copyProgram(
            @PathVariable Long programId,
            @Valid @RequestBody CopyWorkoutProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.copyProgram(
                        currentUserId(), programId, request));
    }

    @PutMapping("/{planId}")
    public UserWorkoutPlanResponse update(
            @PathVariable Long planId,
            @Valid @RequestBody UpdateWorkoutPlanRequest request) {
        return planService.update(currentUserId(), planId, request);
    }

    @PatchMapping("/{planId}/status")
    public UserWorkoutPlanResponse updateStatus(
            @PathVariable Long planId,
            @Valid @RequestBody UpdateWorkoutPlanStatusRequest request) {
        return planService.updateStatus(
                currentUserId(), planId, request);
    }

    @PutMapping("/{planId}/exercises")
    public UserWorkoutPlanResponse replaceExercises(
            @PathVariable Long planId,
            @Valid @RequestBody List<@Valid WorkoutProgramExerciseRequest> requests) {
        return planService.replaceExercises(
                currentUserId(), planId, requests);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> archive(@PathVariable Long planId) {
        planService.archive(currentUserId(), planId);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return Long.parseLong(userId);
    }
}
