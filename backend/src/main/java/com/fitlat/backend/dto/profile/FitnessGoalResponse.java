package com.fitlat.backend.dto.profile;

import com.fitlat.backend.entity.GoalStatus;
import com.fitlat.backend.entity.GoalType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FitnessGoalResponse(
        Long id,
        GoalType goalType,
        BigDecimal targetWeightKg,
        LocalDate targetDate,
        GoalStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}