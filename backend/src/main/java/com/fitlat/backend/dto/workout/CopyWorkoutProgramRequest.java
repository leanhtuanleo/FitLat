
package com.fitlat.backend.dto.workout;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CopyWorkoutProgramRequest(
        @NotBlank @Size(max = 150) String name,
        LocalDate startDate
) {}
