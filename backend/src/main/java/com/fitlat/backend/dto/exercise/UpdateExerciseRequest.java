package com.fitlat.backend.dto.exercise;

import com.fitlat.backend.entity.DifficultyLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record UpdateExerciseRequest(
    @NotBlank @Size(max = 150)
    String nameVi,

    @NotBlank @Size(max = 150)
    String nameEn,

    String description,

    String instructions,

    @NotNull
    DifficultyLevel difficulty,

    @Size(max = 500)
    @Pattern(regexp = "^(https?://.*)?$", message = "Video URL must start with http:// or https://")
    String videoUrl,

    @NotNull @Size(min = 1, max = 20)
    List<@Valid ExerciseMuscleGroupRequest> muscleGroups,

    @NotNull @Size(max = 10)
    List<@Positive Long> equipmentIds
) {}