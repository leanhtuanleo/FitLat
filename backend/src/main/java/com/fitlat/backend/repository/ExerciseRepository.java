
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.Exercise;
import com.fitlat.backend.entity.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    boolean existsByNameEnIgnoreCaseAndActiveTrueAndIdNot(
        String nameEn,
        Long id
    );

    @Query("""
        SELECT DISTINCT e
        FROM Exercise e
        LEFT JOIN e.muscleGroups mg
        LEFT JOIN e.equipment eq
        WHERE e.active = true
          AND (
              :hasKeyword = false
              OR LOWER(e.nameVi) LIKE CONCAT('%', :keyword, '%')
              OR LOWER(e.nameEn) LIKE CONCAT('%', :keyword, '%')
          )
          AND (
              :difficulty IS NULL
              OR e.difficulty = :difficulty
          )
          AND (
              :muscleGroupId IS NULL
              OR mg.muscleGroup.id = :muscleGroupId
          )
          AND (
              :equipmentId IS NULL
              OR eq.equipment.id = :equipmentId
          )
        ORDER BY e.nameEn ASC
    """)
    List<Exercise> search(
        @Param("keyword") String keyword,
        @Param("hasKeyword") boolean hasKeyword,
        @Param("difficulty") DifficultyLevel difficulty,
        @Param("muscleGroupId") Long muscleGroupId,
        @Param("equipmentId") Long equipmentId
    );

    Optional<Exercise> findByIdAndActiveTrue(Long id);

    boolean existsByNameEnIgnoreCaseAndActiveTrue(String nameEn);
}
