package com.fitlat.backend.dto.meal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyNutritionResponse(
        LocalDate date,
        int mealCount,
        BigDecimal totalCalories,
        BigDecimal totalProtein,
        BigDecimal totalCarbs,
        BigDecimal totalFat,
        BigDecimal totalFiber
) {}