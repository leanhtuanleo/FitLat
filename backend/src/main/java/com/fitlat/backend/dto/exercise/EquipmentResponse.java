package com.fitlat.backend.dto.exercise;

public record EquipmentResponse(
    Long id,
    String code,
    String nameVi,
    String nameEn
) {}