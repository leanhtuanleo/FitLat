
package com.fitlat.backend.service;

import com.fitlat.backend.dto.CalorieNeedsResponse;
import com.fitlat.backend.dto.dashboard.DailyDashboardResponse;
import com.fitlat.backend.dto.dashboard.DailyProgressResponse;
import com.fitlat.backend.dto.dashboard.DailyWorkoutSummaryResponse;
import com.fitlat.backend.dto.meal.DailyNutritionResponse;
import com.fitlat.backend.entity.WorkoutSession;
import com.fitlat.backend.entity.WorkoutSessionStatus;
import com.fitlat.backend.repository.WorkoutSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final MealLogService mealLogService;
    private final CalorieCalculationService calorieCalculationService;
    private final WorkoutSessionRepository workoutSessionRepository;

    public DashboardService(
            MealLogService mealLogService,
            CalorieCalculationService calorieCalculationService,
            WorkoutSessionRepository workoutSessionRepository
    ) {
        this.mealLogService = mealLogService;
        this.calorieCalculationService = calorieCalculationService;
        this.workoutSessionRepository = workoutSessionRepository;
    }

    @Transactional(readOnly = true)
    public DailyDashboardResponse getDailyDashboard(
            Long userId,
            LocalDate date
    ) {
        if (date == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date is required"
            );
        }

        // 1. Nutrition summary
        DailyNutritionResponse nutrition =
                mealLogService.getDailyNutrition(userId, date);

        // 2. Calorie needs
        CalorieNeedsResponse calorieNeeds = null;

        try {
            calorieNeeds = calorieCalculationService.calculate(userId);
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode().is5xxServerError()) {
                throw ex;
            }
            // Profile or active goal is not ready:
            // keep the rest of the dashboard available.
        }

        // 3. Workout summary
        List<WorkoutSession> sessions =
                workoutSessionRepository
                        .findAllByUserIdAndSessionDateAndStatusOrderByStartedAtDesc(
                                userId,
                                date,
                                WorkoutSessionStatus.COMPLETED
                        );

        int totalDurationMinutes = sessions.stream()
                .map(WorkoutSession::getDurationMinutes)
                .filter(duration -> duration != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalSetCount = sessions.stream()
                .flatMap(session -> session.getExercises().stream())
                .flatMap(exercise -> exercise.getSetLogs().stream())
                .mapToInt(setLog -> 1)
                .sum();

        DailyWorkoutSummaryResponse workout =
                new DailyWorkoutSummaryResponse(
                        sessions.size(),
                        totalDurationMinutes,
                        totalSetCount
                );

        // 4. Daily calorie progress
        DailyProgressResponse dailyProgress = null;

        if (calorieNeeds != null) {
            BigDecimal caloriesConsumed = nutrition.totalCalories();

            int target = calorieNeeds.dailyCalorieTarget();

            BigDecimal caloriesRemaining = BigDecimal.valueOf(target)
                    .subtract(caloriesConsumed)
                    .max(BigDecimal.ZERO);

            int completionPercentage = target > 0
                    ? caloriesConsumed
                            .multiply(BigDecimal.valueOf(100))
                            .divide(
                                    BigDecimal.valueOf(target),
                                    0,
                                    RoundingMode.HALF_UP
                            )
                            .intValue()
                    : 0;

            dailyProgress = new DailyProgressResponse(
                    caloriesConsumed,
                    target,
                    caloriesRemaining,
                    completionPercentage
            );
        }

        return new DailyDashboardResponse(
                date,
                nutrition,
                calorieNeeds,
                workout,
                dailyProgress
        );
    }
}
