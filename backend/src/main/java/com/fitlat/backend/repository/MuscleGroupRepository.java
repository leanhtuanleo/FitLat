package com.fitlat.backend.repository;

import com.fitlat.backend.entity.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, Long> {

    List<MuscleGroup> findAllByActiveTrueOrderByNameEnAsc();

    Optional<MuscleGroup> findByIdAndActiveTrue(Long id);
}