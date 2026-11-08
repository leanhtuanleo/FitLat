package com.fitlat.backend.dto.meal;

import com.fitlat.backend.entity.MealType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateMealRequest(
        @NotNull
        MealType mealType,

        @NotNull
        LocalDateTime consumedAt,

        @Size(max = 500)
        String note
) {}