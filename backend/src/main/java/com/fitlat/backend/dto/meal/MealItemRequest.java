package com.fitlat.backend.dto.meal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MealItemRequest(
        @NotNull
        @Positive
        Long foodItemId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal quantityGrams
) {}