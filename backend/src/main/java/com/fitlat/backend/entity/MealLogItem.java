package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_log_items")
public class MealLogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_log_id", nullable = false)
    private MealLog mealLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "food_name_snapshot", nullable = false, length = 150)
    private String foodNameSnapshot;

    @Column(name = "quantity_grams", nullable = false, precision = 8, scale = 2)
    private BigDecimal quantityGrams;

    @Column(name = "calories_per_100g", nullable = false, precision = 8, scale = 2)
    private BigDecimal caloriesPer100g;

    @Column(name = "protein_per_100g", nullable = false, precision = 8, scale = 2)
    private BigDecimal proteinPer100g;

    @Column(name = "carbs_per_100g", nullable = false, precision = 8, scale = 2)
    private BigDecimal carbsPer100g;

    @Column(name = "fat_per_100g", nullable = false, precision = 8, scale = 2)
    private BigDecimal fatPer100g;

    @Column(name = "fiber_per_100g", precision = 8, scale = 2)
    private BigDecimal fiberPer100g;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected MealLogItem() {
    }

    public MealLogItem(
            MealLog mealLog,
            FoodItem foodItem,
            BigDecimal quantityGrams
    ) {
        this.mealLog = mealLog;
        this.foodItem = foodItem;
        this.foodNameSnapshot = foodItem.getName();
        this.quantityGrams = quantityGrams;

        // Lưu snapshot dinh dưỡng tại thời điểm ghi nhận bữa ăn.
        this.caloriesPer100g = foodItem.getCaloriesPer100g();
        this.proteinPer100g = foodItem.getProteinPer100g();
        this.carbsPer100g = foodItem.getCarbsPer100g();
        this.fatPer100g = foodItem.getFatPer100g();
        this.fiberPer100g = foodItem.getFiberPer100g();
    }

    public void updateQuantity(BigDecimal quantityGrams) {
        this.quantityGrams = quantityGrams;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public MealLog getMealLog() {
        return mealLog;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public String getFoodNameSnapshot() {
        return foodNameSnapshot;
    }

    public BigDecimal getQuantityGrams() {
        return quantityGrams;
    }

    public BigDecimal getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public BigDecimal getProteinPer100g() {
        return proteinPer100g;
    }

    public BigDecimal getCarbsPer100g() {
        return carbsPer100g;
    }

    public BigDecimal getFatPer100g() {
        return fatPer100g;
    }

    public BigDecimal getFiberPer100g() {
        return fiberPer100g;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}