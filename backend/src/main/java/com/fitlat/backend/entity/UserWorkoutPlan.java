
package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_workout_plans")
public class UserWorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_program_id")
    private WorkoutProgram sourceProgram;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Column(name = "days_per_week", nullable = false)
    private Integer daysPerWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkoutPlanStatus status = WorkoutPlanStatus.ACTIVE;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UserWorkoutPlanExercise> exercises = new ArrayList<>();

    protected UserWorkoutPlan() {}

    public UserWorkoutPlan(
        User user,
        WorkoutProgram sourceProgram,
        String name,
        Integer durationWeeks,
        Integer daysPerWeek,
        LocalDate startDate,
        LocalDate endDate
    ) {
        this.user = user;
        this.sourceProgram = sourceProgram;
        this.name = name;
        this.durationWeeks = durationWeeks;
        this.daysPerWeek = daysPerWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = WorkoutPlanStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addExercise(UserWorkoutPlanExercise exercise) {
        exercises.add(exercise);
    }

    public void clearExercises() {
        exercises.clear();
    }

    public void update(String name, Integer durationWeeks,
                       Integer daysPerWeek, LocalDate startDate,
                       LocalDate endDate) {
        this.name = name;
        this.durationWeeks = durationWeeks;
        this.daysPerWeek = daysPerWeek;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateStatus(WorkoutPlanStatus status) {
        this.status = status;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public WorkoutProgram getSourceProgram() { return sourceProgram; }
    public String getName() { return name; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public Integer getDaysPerWeek() { return daysPerWeek; }
    public WorkoutPlanStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<UserWorkoutPlanExercise> getExercises() { return exercises; }

    public void initialize(User user, WorkoutProgram sourceProgram) {
        this.user = user;
        this.sourceProgram = sourceProgram;
    }
}
