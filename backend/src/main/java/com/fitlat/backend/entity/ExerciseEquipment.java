package com.fitlat.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exercise_equipment")
public class ExerciseEquipment {

    @EmbeddedId
    private ExerciseEquipmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    protected ExerciseEquipment() {}

    public ExerciseEquipment(Exercise exercise, Equipment equipment) {
        this.exercise = exercise;
        this.equipment = equipment;
        this.id = new ExerciseEquipmentId(
            exercise.getId(), equipment.getId()
        );
    }

    public Exercise getExercise() { return exercise; }
    public Equipment getEquipment() { return equipment; }
}