package com.fitlat.backend.dto.profile;

import com.fitlat.backend.entity.ActivityLevel;
import com.fitlat.backend.entity.BiologicalSex;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateUserProfileRequest(

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        BiologicalSex biologicalSex,

        @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
        @DecimalMax(value = "250.0", message = "Height must not exceed 250 cm")
        BigDecimal heightCm,

        @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
        @DecimalMax(value = "500.0", message = "Weight must not exceed 500 kg")
        BigDecimal currentWeightKg,

        ActivityLevel activityLevel
) {
}