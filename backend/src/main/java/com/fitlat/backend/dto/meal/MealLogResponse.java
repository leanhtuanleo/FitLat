package com.fitlat.backend.dto.meal;

import com.fitlat.backend.entity.MealType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MealLogResponse(
        Long id,
        MealType mealType,
        LocalDateTime consumedAt,
        String note,
        List<MealLogItemResponse> items,
        BigDecimal totalCalories,
        BigDecimal totalProtein,
        BigDecimal totalCarbs,
        BigDecimal totalFat,
        BigDecimal totalFiber
) {}