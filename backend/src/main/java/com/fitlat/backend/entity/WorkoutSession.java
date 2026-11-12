
package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    private UserWorkoutPlan workoutPlan;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutSessionStatus status = WorkoutSessionStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<WorkoutSessionExercise> exercises = new ArrayList<>();

    protected WorkoutSession() {}

    public WorkoutSession(User user, UserWorkoutPlan workoutPlan,
                          LocalDate sessionDate, String notes) {
        this.user = user;
        this.workoutPlan = workoutPlan;
        this.sessionDate = sessionDate;
        this.notes = notes;
        this.status = WorkoutSessionStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addExercise(WorkoutSessionExercise exercise) {
        exercises.add(exercise);
    }

    public void updateNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(Integer durationMinutes) {
        this.status = WorkoutSessionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.durationMinutes = durationMinutes;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = WorkoutSessionStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public UserWorkoutPlan getWorkoutPlan() { return workoutPlan; }
    public LocalDate getSessionDate() { return sessionDate; }
    public WorkoutSessionStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<WorkoutSessionExercise> getExercises() { return exercises; }
}
