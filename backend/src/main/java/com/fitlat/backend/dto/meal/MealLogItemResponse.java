package com.fitlat.backend.dto.meal;

import java.math.BigDecimal;

public record MealLogItemResponse(
        Long id,
        Long foodItemId,
        String foodName,
        BigDecimal quantityGrams,
        BigDecimal calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fat,
        BigDecimal fiber
) {}