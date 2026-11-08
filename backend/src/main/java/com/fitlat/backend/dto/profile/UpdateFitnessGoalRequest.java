package com.fitlat.backend.dto.profile;

import com.fitlat.backend.entity.GoalStatus;
import com.fitlat.backend.entity.GoalType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFitnessGoalRequest(

        @NotNull
        GoalType goalType,

        @NotNull
        @DecimalMin("20.0")
        @DecimalMax("500.0")
        BigDecimal targetWeightKg,

        @Future
        LocalDate targetDate,

        @NotNull
        GoalStatus status
) {
}