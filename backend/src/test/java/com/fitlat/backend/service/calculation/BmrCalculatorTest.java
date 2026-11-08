package com.fitlat.backend.service.calculation;

import com.fitlat.backend.entity.BiologicalSex;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BmrCalculatorTest {

    private final BmrCalculator calculator = new BmrCalculator();

    @Test
    void shouldCalculateBmrForMale() {
        int result = calculator.calculate(
                new BigDecimal("75.2"),
                new BigDecimal("175.5"),
                23,
                BiologicalSex.MALE
        );

        assertEquals(1739, result);
    }

    @Test
    void shouldCalculateBmrForFemale() {
        int result = calculator.calculate(
                new BigDecimal("75.2"),
                new BigDecimal("175.5"),
                23,
                BiologicalSex.FEMALE
        );

        assertEquals(1573, result);
    }

    @Test
    void shouldRejectNullInput() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(
                        null,
                        new BigDecimal("175.5"),
                        23,
                        BiologicalSex.MALE
                )
        );
    }

    @Test
    void shouldRejectInvalidWeight() {
        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(
                        BigDecimal.ZERO,
                        new BigDecimal("175.5"),
                        23,
                        BiologicalSex.MALE
                )
        );
    }
}