package com.fitlat.backend.dto.workout.logging;

import java.util.List;

public record WorkoutSessionExerciseResponse(
        Long id,
        Long exerciseId,
        Long planExerciseId,
        String exerciseNameVi,
        String exerciseNameEn,
        Integer orderIndex,
        Integer targetSets,
        Integer targetRepsMin,
        Integer targetRepsMax,
        Integer restSeconds,
        String notes,
        List<WorkoutSetLogResponse> setLogs
) {
}