package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExerciseMuscleGroupId implements Serializable {

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "muscle_group_id")
    private Long muscleGroupId;

    protected ExerciseMuscleGroupId() {}

    public ExerciseMuscleGroupId(Long exerciseId, Long muscleGroupId) {
        this.exerciseId = exerciseId;
        this.muscleGroupId = muscleGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciseMuscleGroupId that)) return false;
        return Objects.equals(exerciseId, that.exerciseId)
            && Objects.equals(muscleGroupId, that.muscleGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exerciseId, muscleGroupId);
    }
}