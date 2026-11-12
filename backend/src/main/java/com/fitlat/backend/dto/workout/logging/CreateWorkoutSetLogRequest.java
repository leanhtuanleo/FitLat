package com.fitlat.backend.dto.workout.logging;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateWorkoutSetLogRequest(
        @NotNull
        @Positive
        Integer setNumber,

        @PositiveOrZero
        Integer reps,

        @DecimalMin("0.0")
        BigDecimal weightKg,

        @PositiveOrZero
        Integer durationSeconds,

        @DecimalMin("0.0")
        BigDecimal distanceMeters,

        @DecimalMin("1.0")
        @DecimalMax("10.0")
        BigDecimal rpe,

        @Size(max = 1000)
        String notes
) {
}