package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.ActivityLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TdeeCalculatorTest {

    private final TdeeCalculator calculator = new TdeeCalculator();

    @Test
    void shouldCalculateTdeeForAllActivityLevels() {
        assertEquals(2400, calculator.calculate(2000, ActivityLevel.SEDENTARY));
        assertEquals(2750, calculator.calculate(2000, ActivityLevel.LIGHTLY_ACTIVE));
        assertEquals(3100, calculator.calculate(2000, ActivityLevel.MODERATELY_ACTIVE));
        assertEquals(3450, calculator.calculate(2000, ActivityLevel.VERY_ACTIVE));
        assertEquals(3800, calculator.calculate(2000, ActivityLevel.EXTRA_ACTIVE));
    }

    @Test
    void shouldRejectInvalidBmr() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(0, ActivityLevel.MODERATELY_ACTIVE)
        );
    }

    @Test
    void shouldRejectNullActivityLevel() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(2000, null)
        );
    }
}