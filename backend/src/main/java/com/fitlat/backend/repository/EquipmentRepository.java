package com.fitlat.backend.repository;

import com.fitlat.backend.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findAllByActiveTrueOrderByNameEnAsc();

    Optional<Equipment> findByIdAndActiveTrue(Long id);
}