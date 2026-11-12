
package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_session_exercises")
public class WorkoutSessionExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_exercise_id")
    private UserWorkoutPlanExercise planExercise;

    @Column(name = "exercise_name_vi", nullable = false, length = 150)
    private String exerciseNameVi;

    @Column(name = "exercise_name_en", nullable = false, length = 150)
    private String exerciseNameEn;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "target_sets")
    private Integer targetSets;

    @Column(name = "target_reps_min")
    private Integer targetRepsMin;

    @Column(name = "target_reps_max")
    private Integer targetRepsMax;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "sessionExercise",
               cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNumber ASC")
    private List<WorkoutSetLog> setLogs = new ArrayList<>();

    protected WorkoutSessionExercise() {}

    public WorkoutSessionExercise(
            WorkoutSession session,
            Exercise exercise,
            UserWorkoutPlanExercise planExercise,
            Integer orderIndex,
            Integer targetSets,
            Integer targetRepsMin,
            Integer targetRepsMax,
            Integer restSeconds,
            String notes
    ) {
        this.session = session;
        this.exercise = exercise;
        this.planExercise = planExercise;
        this.exerciseNameVi = exercise.getNameVi();
        this.exerciseNameEn = exercise.getNameEn();
        this.orderIndex = orderIndex;
        this.targetSets = targetSets;
        this.targetRepsMin = targetRepsMin;
        this.targetRepsMax = targetRepsMax;
        this.restSeconds = restSeconds;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public void addSetLog(WorkoutSetLog setLog) {
        setLogs.add(setLog);
    }

    public Long getId() { return id; }
    public WorkoutSession getSession() { return session; }
    public Exercise getExercise() { return exercise; }
    public UserWorkoutPlanExercise getPlanExercise() { return planExercise; }
    public String getExerciseNameVi() { return exerciseNameVi; }
    public String getExerciseNameEn() { return exerciseNameEn; }
    public Integer getOrderIndex() { return orderIndex; }
    public Integer getTargetSets() { return targetSets; }
    public Integer getTargetRepsMin() { return targetRepsMin; }
    public Integer getTargetRepsMax() { return targetRepsMax; }
    public Integer getRestSeconds() { return restSeconds; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<WorkoutSetLog> getSetLogs() { return setLogs; }
}
