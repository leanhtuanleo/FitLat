
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.WorkoutSession;
import com.fitlat.backend.entity.WorkoutSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository
        extends JpaRepository<WorkoutSession, Long> {
                
    List<WorkoutSession> findAllByUserIdOrderBySessionDateDescStartedAtDesc(
            Long userId
    );

    List<WorkoutSession> findAllByUserIdAndSessionDate(
            Long userId,
            LocalDate sessionDate
    );

    Optional<WorkoutSession> findByIdAndUserId(
            Long id,
            Long userId
    );

    List<WorkoutSession> findAllByUserIdAndStatusOrderByStartedAtDesc(
            Long userId,
            WorkoutSessionStatus status
    );

    List<WorkoutSession> findAllByUserIdAndSessionDateAndStatusOrderByStartedAtDesc(
        Long userId,
        LocalDate sessionDate,
        WorkoutSessionStatus status
    );
}
