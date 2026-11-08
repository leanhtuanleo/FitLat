package com.fitlat.backend.dto.food;

import com.fitlat.backend.entity.FoodCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateFoodItemRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        FoodCategory category,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal caloriesPer100g,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal proteinPer100g,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal carbsPer100g,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal fatPer100g,

        @DecimalMin("0.0")
        BigDecimal fiberPer100g
) {}