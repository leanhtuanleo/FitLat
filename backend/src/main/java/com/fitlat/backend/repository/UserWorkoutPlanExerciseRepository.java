
package com.fitlat.backend.repository;

import com.fitlat.backend.entity.UserWorkoutPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWorkoutPlanExerciseRepository
        extends JpaRepository<UserWorkoutPlanExercise, Long> {

    List<UserWorkoutPlanExercise>
    findAllByPlanIdOrderByDayNumberAscOrderIndexAsc(Long planId);
}
