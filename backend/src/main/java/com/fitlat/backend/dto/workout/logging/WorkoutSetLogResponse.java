package com.fitlat.backend.dto.workout.logging;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkoutSetLogResponse(
        Long id,
        Integer setNumber,
        Integer reps,
        BigDecimal weightKg,
        Integer durationSeconds,
        BigDecimal distanceMeters,
        BigDecimal rpe,
        String notes,
        LocalDateTime createdAt
) {
}