
package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_programs")
public class WorkoutProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_vi", nullable = false, length = 150)
    private String nameVi;

    @Column(name = "name_en", nullable = false, length = 150)
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Column(name = "days_per_week", nullable = false)
    private Integer daysPerWeek;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<WorkoutProgramExercise> exercises = new ArrayList<>();

    protected WorkoutProgram() {}

    public WorkoutProgram(
        String nameVi,
        String nameEn,
        String description,
        DifficultyLevel difficulty,
        Integer durationWeeks,
        Integer daysPerWeek
    ) {
        this.nameVi = nameVi;
        this.nameEn = nameEn;
        this.description = description;
        this.difficulty = difficulty;
        this.durationWeeks = durationWeeks;
        this.daysPerWeek = daysPerWeek;
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

    public void addExercise(WorkoutProgramExercise exercise) {
        exercises.add(exercise);
    }

    public void clearExercises() {
        exercises.clear();
    }

    public Long getId() { return id; }
    public String getNameVi() { return nameVi; }
    public String getNameEn() { return nameEn; }
    public String getDescription() { return description; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public Integer getDurationWeeks() { return durationWeeks; }
    public Integer getDaysPerWeek() { return daysPerWeek; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<WorkoutProgramExercise> getExercises() { return exercises; }

    public void update(String nameVi, String nameEn, String description,
                       DifficultyLevel difficulty, Integer durationWeeks,
                       Integer daysPerWeek) {
        this.nameVi = nameVi;
        this.nameEn = nameEn;
        this.description = description;
        this.difficulty = difficulty;
        this.durationWeeks = durationWeeks;
        this.daysPerWeek = daysPerWeek;
    }

    public void deactivate() {
        this.active = false;
    }
}
