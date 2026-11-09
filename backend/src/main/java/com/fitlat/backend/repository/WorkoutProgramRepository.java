
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.WorkoutProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutProgramRepository
        extends JpaRepository<WorkoutProgram, Long> {

    List<WorkoutProgram> findAllByActiveTrueOrderByIdDesc();

    boolean existsByNameEnIgnoreCaseAndActiveTrue(String nameEn);

    boolean existsByNameEnIgnoreCaseAndActiveTrueAndIdNot(
            String nameEn, Long id
    );
}
