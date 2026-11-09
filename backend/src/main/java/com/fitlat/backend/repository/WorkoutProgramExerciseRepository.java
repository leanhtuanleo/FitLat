
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.WorkoutProgramExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutProgramExerciseRepository
        extends JpaRepository<WorkoutProgramExercise, Long> {

    List<WorkoutProgramExercise>
    findAllByProgramIdOrderByDayNumberAscOrderIndexAsc(Long programId);
}
