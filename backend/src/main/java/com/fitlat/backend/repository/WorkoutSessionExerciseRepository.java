
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.WorkoutSessionExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface WorkoutSessionExerciseRepository
        extends JpaRepository<WorkoutSessionExercise, Long> {

    List<WorkoutSessionExercise>
    findAllBySessionIdOrderByOrderIndexAsc(Long sessionId);

    Optional<WorkoutSessionExercise> findByIdAndSessionId(
        Long id,
        Long sessionId
    );
}
