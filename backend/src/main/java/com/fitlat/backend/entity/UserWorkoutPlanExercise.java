
package com.fitlat.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_workout_plan_exercises")
public class UserWorkoutPlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private UserWorkoutPlan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "target_sets", nullable = false)
    private Integer targetSets;

    @Column(name = "reps_min", nullable = false)
    private Integer repsMin;

    @Column(name = "reps_max", nullable = false)
    private Integer repsMax;

    @Column(name = "rest_seconds", nullable = false)
    private Integer restSeconds = 60;

    @Column(length = 500)
    private String notes;

    protected UserWorkoutPlanExercise() {}

    public UserWorkoutPlanExercise(
            UserWorkoutPlan plan, Exercise exercise,
            Integer dayNumber, Integer orderIndex,
            Integer targetSets, Integer repsMin,
            Integer repsMax, Integer restSeconds, String notes) {
        this.plan = plan;
        this.exercise = exercise;
        this.dayNumber = dayNumber;
        this.orderIndex = orderIndex;
        this.targetSets = targetSets;
        this.repsMin = repsMin;
        this.repsMax = repsMax;
        this.restSeconds = restSeconds;
        this.notes = notes;
    }

    public Long getId() { return id; }
    public Exercise getExercise() { return exercise; }
    public Integer getDayNumber() { return dayNumber; }
    public Integer getOrderIndex() { return orderIndex; }
    public Integer getTargetSets() { return targetSets; }
    public Integer getRepsMin() { return repsMin; }
    public Integer getRepsMax() { return repsMax; }
    public Integer getRestSeconds() { return restSeconds; }
    public String getNotes() { return notes; }
}
