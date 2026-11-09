
package com.fitlat.backend.dto.workout;

import com.fitlat.backend.entity.DifficultyLevel;

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutProgramResponse(
        Long id,
        String nameVi,
        String nameEn,
        String description,
        DifficultyLevel difficulty,
        Integer durationWeeks,
        Integer daysPerWeek,
        List<WorkoutProgramExerciseResponse> exercises,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
