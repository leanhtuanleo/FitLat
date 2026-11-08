
package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.GoalType;

public class CalorieTargetCalculator {

    private static final int CALORIE_ADJUSTMENT = 300;

    public int calculate(int tdee, GoalType goalType) {
        if (tdee <= 0 || goalType == null) {
            throw new IllegalArgumentException("Invalid calorie target input");
        }

        return switch (goalType) {
            case LOSE_WEIGHT -> tdee - CALORIE_ADJUSTMENT;
            case GAIN_WEIGHT -> tdee + CALORIE_ADJUSTMENT;
            case MAINTAIN_WEIGHT -> tdee;
        };
    }
}
