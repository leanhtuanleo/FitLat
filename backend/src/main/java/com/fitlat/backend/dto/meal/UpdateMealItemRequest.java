package com.fitlat.backend.dto.meal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateMealItemRequest(
        @NotNull
        @DecimalMin("0.01")
        BigDecimal quantityGrams
) {}