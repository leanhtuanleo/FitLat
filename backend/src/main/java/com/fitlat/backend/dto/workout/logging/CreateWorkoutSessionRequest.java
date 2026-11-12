package com.fitlat.backend.dto.workout.logging;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateWorkoutSessionRequest(
        @Positive
        Long workoutPlanId,

        LocalDate sessionDate,

        @Size(max = 1000)
        String notes
) {
}