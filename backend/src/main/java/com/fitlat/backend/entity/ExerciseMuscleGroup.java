package com.fitlat.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exercise_muscle_groups")
public class ExerciseMuscleGroup {

    @EmbeddedId
    private ExerciseMuscleGroupId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("muscleGroupId")
    @JoinColumn(name = "muscle_group_id")
    private MuscleGroup muscleGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "muscle_role", nullable = false, length = 20)
    private MuscleRole muscleRole;

    protected ExerciseMuscleGroup() {}

    public ExerciseMuscleGroup(
        Exercise exercise,
        MuscleGroup muscleGroup,
        MuscleRole muscleRole
    ) {
        this.exercise = exercise;
        this.muscleGroup = muscleGroup;
        this.muscleRole = muscleRole;
        this.id = new ExerciseMuscleGroupId(
            exercise.getId(), muscleGroup.getId()
        );
    }

    public Exercise getExercise() { return exercise; }
    public MuscleGroup getMuscleGroup() { return muscleGroup; }
    public MuscleRole getMuscleRole() { return muscleRole; }
}