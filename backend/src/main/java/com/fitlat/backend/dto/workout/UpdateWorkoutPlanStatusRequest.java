
package com.fitlat.backend.dto.workout;

import com.fitlat.backend.entity.WorkoutPlanStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkoutPlanStatusRequest(
        @NotNull WorkoutPlanStatus status
) {}
