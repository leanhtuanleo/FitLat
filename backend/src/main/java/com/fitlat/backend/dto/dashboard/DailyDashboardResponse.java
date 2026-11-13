package com.fitlat.backend.dto.dashboard;

import com.fitlat.backend.dto.CalorieNeedsResponse;
import com.fitlat.backend.dto.meal.DailyNutritionResponse;

import java.time.LocalDate;

public record DailyDashboardResponse(
        LocalDate date,
        DailyNutritionResponse nutrition,
        CalorieNeedsResponse calorieNeeds,
        DailyWorkoutSummaryResponse workout,
        DailyProgressResponse dailyProgress
) {
}