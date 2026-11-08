
package com.fitlat.backend.dto;

import com.fitlat.backend.entity.GoalType;

public record CalorieNeedsResponse(
        int age,
        int bmr,
        int tdee,
        int dailyCalorieTarget,
        GoalType goalType
) {
}
