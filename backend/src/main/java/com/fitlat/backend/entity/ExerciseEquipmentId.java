package com.fitlat.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExerciseEquipmentId implements Serializable {

    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "equipment_id")
    private Long equipmentId;

    protected ExerciseEquipmentId() {}

    public ExerciseEquipmentId(Long exerciseId, Long equipmentId) {
        this.exerciseId = exerciseId;
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExerciseEquipmentId that)) return false;
        return Objects.equals(exerciseId, that.exerciseId)
            && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exerciseId, equipmentId);
    }
}