package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 20)
    private MealType mealType;

    @Column(name = "consumed_at", nullable = false)
    private LocalDateTime consumedAt;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MealLog() {
    }

    public MealLog(
            User user,
            MealType mealType,
            LocalDateTime consumedAt,
            String note
    ) {
        this.user = user;
        this.mealType = mealType;
        this.consumedAt = consumedAt;
        this.note = note;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(
            MealType mealType,
            LocalDateTime consumedAt,
            String note
    ) {
        this.mealType = mealType;
        this.consumedAt = consumedAt;
        this.note = note;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MealType getMealType() {
        return mealType;
    }

    public LocalDateTime getConsumedAt() {
        return consumedAt;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}