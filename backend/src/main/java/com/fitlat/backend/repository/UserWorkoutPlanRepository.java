
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.UserWorkoutPlan;
import com.fitlat.backend.entity.WorkoutPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWorkoutPlanRepository
        extends JpaRepository<UserWorkoutPlan, Long> {

    List<UserWorkoutPlan>
    findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserWorkoutPlan>
    findAllByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, WorkoutPlanStatus status
    );

    Optional<UserWorkoutPlan> findByIdAndUserId(
            Long id, Long userId
    );
}
