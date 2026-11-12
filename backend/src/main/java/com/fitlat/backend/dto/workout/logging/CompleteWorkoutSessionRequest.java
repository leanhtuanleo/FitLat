package com.fitlat.backend.dto.workout.logging;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CompleteWorkoutSessionRequest(
        @PositiveOrZero
        Integer durationMinutes,

        @Size(max = 1000)
        String notes
) {
}