package com.fitlat.backend.dto.workout.logging;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record WorkoutProgressResponse(
        Long exerciseId,
        String exerciseNameVi,
        String exerciseNameEn,
        Long totalSessions,
        Long totalSets,
        BigDecimal bestWeightKg,
        Integer bestReps,
        LocalDate latestSessionDate,
        List<ProgressPoint> history
) {
    public record ProgressPoint(
            Long sessionId,
            LocalDate sessionDate,
            Integer setNumber,
            Integer reps,
            BigDecimal weightKg,
            BigDecimal rpe
    ) {
    }
}