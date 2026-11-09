package com.fitlat.backend.dto.exercise;

import com.fitlat.backend.entity.MuscleRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExerciseMuscleGroupRequest(
    @NotNull @Positive Long muscleGroupId,
    @NotNull MuscleRole muscleRole
) {}