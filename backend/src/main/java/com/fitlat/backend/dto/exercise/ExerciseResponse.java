package com.fitlat.backend.dto.exercise;

import com.fitlat.backend.entity.DifficultyLevel;
import java.time.LocalDateTime;
import java.util.List;

public record ExerciseResponse(
    Long id,
    String nameVi,
    String nameEn,
    String description,
    String instructions,
    DifficultyLevel difficulty,
    String videoUrl,
    List<ExerciseMuscleGroupResponse> muscleGroups,
    List<EquipmentResponse> equipment,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}