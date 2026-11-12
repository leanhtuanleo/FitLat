package com.fitlat.backend.controller;

import com.fitlat.backend.dto.workout.logging.*;
import com.fitlat.backend.service.WorkoutSessionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class WorkoutSessionController {

    private final WorkoutSessionService sessionService;

    public WorkoutSessionController(WorkoutSessionService sessionService) {
        this.sessionService = sessionService;
    }

    // 1. Get all workout sessions
    @GetMapping("/workout-sessions")
    public List<WorkoutSessionResponse> getAll() {
        return sessionService.getAll(currentUserId());
    }

    // 2. Get workout session detail
    @GetMapping("/workout-sessions/{sessionId}")
    public WorkoutSessionResponse getById(
            @PathVariable Long sessionId) {
        return sessionService.getById(currentUserId(), sessionId);
    }

    // 3. Create workout session
    @PostMapping("/workout-sessions")
    public ResponseEntity<WorkoutSessionResponse> create(
            @Valid @RequestBody CreateWorkoutSessionRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.create(currentUserId(), request));
    }

    // 4. Add exercise to workout session
    @PostMapping("/workout-sessions/{sessionId}/exercises")
    public ResponseEntity<WorkoutSessionResponse> addExercise(
            @PathVariable Long sessionId,
            @Valid @RequestBody AddSessionExerciseRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.addExercise(
                        currentUserId(), sessionId, request));
    }

    // 5. Add set log
    @PostMapping(
            "/workout-sessions/{sessionId}/exercises/{sessionExerciseId}/sets")
    public ResponseEntity<WorkoutSessionResponse> addSetLog(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @Valid @RequestBody CreateWorkoutSetLogRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.addSetLog(
                        currentUserId(),
                        sessionId,
                        sessionExerciseId,
                        request));
    }

    // 6. Update set log
    @PutMapping(
            "/workout-sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setLogId}")
    public WorkoutSessionResponse updateSetLog(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @PathVariable Long setLogId,
            @Valid @RequestBody UpdateWorkoutSetLogRequest request) {

        return sessionService.updateSetLog(
                currentUserId(),
                sessionId,
                sessionExerciseId,
                setLogId,
                request);
    }

    // 7. Delete set log
    @DeleteMapping(
            "/workout-sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setLogId}")
    public ResponseEntity<Void> deleteSetLog(
            @PathVariable Long sessionId,
            @PathVariable Long sessionExerciseId,
            @PathVariable Long setLogId) {

        sessionService.deleteSetLog(
                currentUserId(),
                sessionId,
                sessionExerciseId,
                setLogId);

        return ResponseEntity.noContent().build();
    }

    // 8. Complete workout session
    @PatchMapping("/workout-sessions/{sessionId}/complete")
    public WorkoutSessionResponse complete(
            @PathVariable Long sessionId,
            @Valid @RequestBody CompleteWorkoutSessionRequest request) {

        return sessionService.complete(
                currentUserId(), sessionId, request);
    }

    // 9. Cancel workout session
    @PatchMapping("/workout-sessions/{sessionId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable Long sessionId) {

        sessionService.cancel(currentUserId(), sessionId);

        return ResponseEntity.noContent().build();
    }

    // 10. Get exercise progress
    @GetMapping("/workout-progress/exercises/{exerciseId}")
    public WorkoutProgressResponse getExerciseProgress(
            @PathVariable Long exerciseId) {

        return sessionService.getExerciseProgress(
                currentUserId(), exerciseId);
    }

    // Get current user ID from JWT
    private Long currentUserId() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return Long.parseLong(userId);
    }
}