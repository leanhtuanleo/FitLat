package com.fitlat.backend.dto.exercise;

import com.fitlat.backend.entity.MuscleRole;

public record ExerciseMuscleGroupResponse(
    Long muscleGroupId,
    String code,
    String nameVi,
    String nameEn,
    MuscleRole muscleRole
) {}