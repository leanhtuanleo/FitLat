package com.fitlat.backend.dto.exercise;

public record MuscleGroupResponse(
    Long id,
    String code,
    String nameVi,
    String nameEn
) {}