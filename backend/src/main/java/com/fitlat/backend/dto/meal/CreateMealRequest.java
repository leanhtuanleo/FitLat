package com.fitlat.backend.dto.meal;

import com.fitlat.backend.entity.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMealRequest(
        @NotNull
        MealType mealType,

        @NotNull
        LocalDateTime consumedAt,

        @Size(max = 500)
        String note,

        @NotNull
        @Size(min = 1, max = 50)
        List<@Valid MealItemRequest> items
) {}