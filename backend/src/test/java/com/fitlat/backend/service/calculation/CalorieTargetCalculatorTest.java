package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.GoalType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalorieTargetCalculatorTest {

    private final CalorieTargetCalculator calculator =
            new CalorieTargetCalculator();

    @Test
    void shouldCalculateTargetForAllGoalTypes() {
        assertEquals(2200, calculator.calculate(2500, GoalType.LOSE_WEIGHT));
        assertEquals(2800, calculator.calculate(2500, GoalType.GAIN_WEIGHT));
        assertEquals(2500, calculator.calculate(2500, GoalType.MAINTAIN_WEIGHT));
    }

    @Test
    void shouldRejectInvalidTdee() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(0, GoalType.LOSE_WEIGHT)
        );
    }

    @Test
    void shouldRejectNullGoalType() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(2500, null)
        );
    }
}