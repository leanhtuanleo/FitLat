
package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "workout_set_logs")
public class WorkoutSetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_exercise_id", nullable = false)
    private WorkoutSessionExercise sessionExercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    private Integer reps;

    @Column(name = "weight_kg", precision = 7, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "distance_meters", precision = 10, scale = 2)
    private BigDecimal distanceMeters;

    @Column(precision = 3, scale = 1)
    private BigDecimal rpe;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected WorkoutSetLog() {}

    public WorkoutSetLog(
            WorkoutSessionExercise sessionExercise,
            Integer setNumber,
            Integer reps,
            BigDecimal weightKg,
            Integer durationSeconds,
            BigDecimal distanceMeters,
            BigDecimal rpe,
            String notes
    ) {
        this.sessionExercise = sessionExercise;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weightKg = weightKg;
        this.durationSeconds = durationSeconds;
        this.distanceMeters = distanceMeters;
        this.rpe = rpe;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public WorkoutSessionExercise getSessionExercise() { return sessionExercise; }
    public Integer getSetNumber() { return setNumber; }
    public Integer getReps() { return reps; }
    public BigDecimal getWeightKg() { return weightKg; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public BigDecimal getDistanceMeters() { return distanceMeters; }
    public BigDecimal getRpe() { return rpe; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void update(
            Integer reps,
            BigDecimal weightKg,
            Integer durationSeconds,
            BigDecimal distanceMeters,
            BigDecimal rpe,
            String notes) {
        this.reps = reps;
        this.weightKg = weightKg;
        this.durationSeconds = durationSeconds;
        this.distanceMeters = distanceMeters;
        this.rpe = rpe;
        this.notes = notes;
    }
}
