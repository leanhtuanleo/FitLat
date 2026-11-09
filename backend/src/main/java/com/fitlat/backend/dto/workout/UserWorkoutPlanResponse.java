
package com.fitlat.backend.dto.workout;

import com.fitlat.backend.entity.WorkoutPlanStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserWorkoutPlanResponse(
        Long id,
        Long sourceProgramId,
        String name,
        Integer durationWeeks,
        Integer daysPerWeek,
        WorkoutPlanStatus status,
        LocalDate startDate,
        LocalDate endDate,
        List<WorkoutProgramExerciseResponse> exercises,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
