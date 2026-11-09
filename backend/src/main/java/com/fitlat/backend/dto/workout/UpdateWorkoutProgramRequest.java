
package com.fitlat.backend.dto.workout;

import com.fitlat.backend.entity.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateWorkoutProgramRequest(
        @NotBlank @Size(max = 150) String nameVi,
        @NotBlank @Size(max = 150) String nameEn,
        String description,
        @NotNull DifficultyLevel difficulty,
        @NotNull @Positive Integer durationWeeks,
        @NotNull @Min(1) @Max(7) Integer daysPerWeek,
        @NotEmpty List<@Valid WorkoutProgramExerciseRequest> exercises
) {}
