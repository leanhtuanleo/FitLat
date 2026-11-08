
package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.ActivityLevel;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TdeeCalculator {

    public int calculate(int bmr, ActivityLevel activityLevel) {
        if (bmr <= 0 || activityLevel == null) {
            throw new IllegalArgumentException("Invalid TDEE input");
        }

        BigDecimal activityFactor = switch (activityLevel) {
            case SEDENTARY -> new BigDecimal("1.2");
            case LIGHTLY_ACTIVE -> new BigDecimal("1.375");
            case MODERATELY_ACTIVE -> new BigDecimal("1.55");
            case VERY_ACTIVE -> new BigDecimal("1.725");
            case EXTRA_ACTIVE -> new BigDecimal("1.9");
        };

        return BigDecimal.valueOf(bmr)
                .multiply(activityFactor)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
