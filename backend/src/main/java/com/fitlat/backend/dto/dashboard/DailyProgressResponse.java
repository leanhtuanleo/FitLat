package com.fitlat.backend.dto.dashboard;

import java.math.BigDecimal;

public record DailyProgressResponse(
        BigDecimal caloriesConsumed,
        int dailyCalorieTarget,
        BigDecimal caloriesRemaining,
        int completionPercentage
) {
}