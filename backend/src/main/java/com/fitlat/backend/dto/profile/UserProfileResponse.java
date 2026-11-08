package com.fitlat.backend.dto.profile;

import com.fitlat.backend.entity.ActivityLevel;
import com.fitlat.backend.entity.BiologicalSex;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        LocalDate dateOfBirth,
        BiologicalSex biologicalSex,
        BigDecimal heightCm,
        BigDecimal currentWeightKg,
        ActivityLevel activityLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}