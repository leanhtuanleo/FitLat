package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exercises")
public class Exercise {
    public Exercise(
    String nameVi,
    String nameEn,
    String description,
    String instructions,
    DifficultyLevel difficulty,
    String videoUrl
    ) {
        this.nameVi = nameVi;
        this.nameEn = nameEn;
        this.description = description;
        this.instructions = instructions;
        this.difficulty = difficulty;
        this.videoUrl = videoUrl;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_vi", nullable = false, length = 150)
    private String nameVi;

    @Column(name = "name_en", nullable = false, length = 150)
    private String nameEn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty = DifficultyLevel.BEGINNER;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
        mappedBy = "exercise",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ExerciseMuscleGroup> muscleGroups = new HashSet<>();

    @OneToMany(
        mappedBy = "exercise",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<ExerciseEquipment> equipment = new HashSet<>();

    protected Exercise() {}

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
        String nameVi,
        String nameEn,
        String description,
        String instructions,
        DifficultyLevel difficulty,
        String videoUrl
    ) {
        this.nameVi = nameVi;
        this.nameEn = nameEn;
        this.description = description;
        this.instructions = instructions;
        this.difficulty = difficulty;
        this.videoUrl = videoUrl;
    }

    public void addMuscleGroup(
        MuscleGroup muscleGroup,
        MuscleRole role
    ) {
        ExerciseMuscleGroup link =
            new ExerciseMuscleGroup(this, muscleGroup, role);
        muscleGroups.add(link);
    }

    public void addEquipment(Equipment equipmentItem) {
        ExerciseEquipment link =
            new ExerciseEquipment(this, equipmentItem);
        equipment.add(link);
    }

    public void clearAssociations() {
        muscleGroups.clear();
        equipment.clear();
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() { return id; }
    public String getNameVi() { return nameVi; }
    public String getNameEn() { return nameEn; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public DifficultyLevel getDifficulty() { return difficulty; }
    public String getVideoUrl() { return videoUrl; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<ExerciseMuscleGroup> getMuscleGroups() { return muscleGroups; }
    public Set<ExerciseEquipment> getEquipment() { return equipment; }
}