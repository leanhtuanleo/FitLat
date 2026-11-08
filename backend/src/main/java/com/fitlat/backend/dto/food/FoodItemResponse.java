package com.fitlat.backend.dto.food;

import com.fitlat.backend.entity.FoodCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FoodItemResponse(
        Long id,
        String name,
        FoodCategory category,
        BigDecimal caloriesPer100g,
        BigDecimal proteinPer100g,
        BigDecimal carbsPer100g,
        BigDecimal fatPer100g,
        BigDecimal fiberPer100g,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}