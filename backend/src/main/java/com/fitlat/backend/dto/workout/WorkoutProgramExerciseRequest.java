
package com.fitlat.backend.dto.workout;

import jakarta.validation.constraints.*;

public record WorkoutProgramExerciseRequest(
        @NotNull @Positive Long exerciseId,
        @NotNull @Min(1) @Max(7) Integer dayNumber,
        @NotNull @Positive Integer orderIndex,
        @NotNull @Positive Integer targetSets,
        @NotNull @Positive Integer repsMin,
        @NotNull @Positive Integer repsMax,
        @NotNull @Min(0) Integer restSeconds,
        @Size(max = 500) String notes
) {}
