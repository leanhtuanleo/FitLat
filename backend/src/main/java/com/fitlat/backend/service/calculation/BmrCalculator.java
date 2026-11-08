
package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.BiologicalSex;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BmrCalculator {

    public int calculate(
            BigDecimal weightKg,
            BigDecimal heightCm,
            int age,
            BiologicalSex biologicalSex
    ) {
        if (weightKg == null || heightCm == null || biologicalSex == null) {
            throw new IllegalArgumentException("Profile data is incomplete");
        }

        if (weightKg.signum() <= 0 || heightCm.signum() <= 0 || age < 0) {
            throw new IllegalArgumentException("Invalid calculation input");
        }

        BigDecimal bmr = weightKg.multiply(BigDecimal.TEN)
                .add(heightCm.multiply(new BigDecimal("6.25")))
                .subtract(BigDecimal.valueOf(5L * age));

        if (biologicalSex == BiologicalSex.MALE) {
            bmr = bmr.add(BigDecimal.valueOf(5));
        } else {
            bmr = bmr.subtract(BigDecimal.valueOf(161));
        }

        return bmr.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
