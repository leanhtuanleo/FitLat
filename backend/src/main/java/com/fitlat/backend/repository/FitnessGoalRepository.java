package com.fitlat.backend.repository;

import com.fitlat.backend.entity.FitnessGoal;
import com.fitlat.backend.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FitnessGoalRepository
        extends JpaRepository<FitnessGoal, Long> {

    boolean existsByUserIdAndStatusAndIdNot(
        Long userId,
        GoalStatus status,
        Long id
    );

    List<FitnessGoal> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<FitnessGoal> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndStatus(Long userId, GoalStatus status);
}

