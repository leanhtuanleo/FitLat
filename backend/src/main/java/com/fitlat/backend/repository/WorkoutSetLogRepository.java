
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.WorkoutSetLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface WorkoutSetLogRepository
        extends JpaRepository<WorkoutSetLog, Long> {

    List<WorkoutSetLog>
    findAllBySessionExerciseIdOrderBySetNumberAsc(Long sessionExerciseId);
    Optional<WorkoutSetLog> findByIdAndSessionExerciseId(
        Long id,
        Long sessionExerciseId
    );
}
