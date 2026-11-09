package com.fitlat.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "muscle_groups")
public class MuscleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name_vi", nullable = false, length = 100)
    private String nameVi;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected MuscleGroup() {}

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getNameVi() { return nameVi; }
    public String getNameEn() { return nameEn; }
    public boolean isActive() { return active; }
}