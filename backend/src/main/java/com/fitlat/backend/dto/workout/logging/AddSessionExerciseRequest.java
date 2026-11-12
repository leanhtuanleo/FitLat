package com.fitlat.backend.dto.workout.logging;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AddSessionExerciseRequest(
        @NotNull
        @Positive
        Long exerciseId,

        @Positive
        Long planExerciseId,

        @NotNull
        @Positive
        Integer orderIndex,

        @NotNull
        @Positive
        Integer targetSets,

        @Positive
        Integer targetRepsMin,

        @Positive
        Integer targetRepsMax,

        @NotNull
        @PositiveOrZero
        Integer restSeconds,

        @Size(max = 1000)
        String notes
) {
}