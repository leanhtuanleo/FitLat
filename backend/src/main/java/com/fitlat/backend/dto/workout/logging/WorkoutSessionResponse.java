package com.fitlat.backend.dto.workout.logging;

import com.fitlat.backend.entity.WorkoutSessionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSessionResponse(
        Long id,
        Long workoutPlanId,
        LocalDate sessionDate,
        WorkoutSessionStatus status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        Integer durationMinutes,
        String notes,
        List<WorkoutSessionExerciseResponse> exercises
) {
}