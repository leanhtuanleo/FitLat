
package com.fitlat.backend.dto.workout;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateWorkoutPlanRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Positive Integer durationWeeks,
        @NotNull @Min(1) @Max(7) Integer daysPerWeek,
        LocalDate startDate,
        LocalDate endDate
) {}
