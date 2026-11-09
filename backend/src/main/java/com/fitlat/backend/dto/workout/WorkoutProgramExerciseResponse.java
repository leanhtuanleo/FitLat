
package com.fitlat.backend.dto.workout;

public record WorkoutProgramExerciseResponse(
        Long id,
        Long exerciseId,
        String exerciseNameVi,
        String exerciseNameEn,
        Integer dayNumber,
        Integer orderIndex,
        Integer targetSets,
        Integer repsMin,
        Integer repsMax,
        Integer restSeconds,
        String notes
) {}
