package com.fitlat.backend.dto.profile;

import com.fitlat.backend.entity.GoalType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFitnessGoalRequest(

        @NotNull(message = "Goal type is required")
        GoalType goalType,

        @NotNull(message = "Target weight is required")
        @DecimalMin(value = "20.0")
        @DecimalMax(value = "500.0")
        BigDecimal targetWeightKg,

        @Future(message = "Target date must be in the future")
        LocalDate targetDate
) {
}