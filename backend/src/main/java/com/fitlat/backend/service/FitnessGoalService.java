
package com.fitlat.backend.service;

import com.fitlat.backend.dto.profile.CreateFitnessGoalRequest;
import com.fitlat.backend.dto.profile.FitnessGoalResponse;
import com.fitlat.backend.dto.profile.UpdateFitnessGoalRequest;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.FitnessGoalRepository;
import com.fitlat.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FitnessGoalService {

    private final FitnessGoalRepository goalRepository;
    private final UserRepository userRepository;

    public FitnessGoalService(
            FitnessGoalRepository goalRepository,
            UserRepository userRepository
    ) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<FitnessGoalResponse> getMyGoals(Long userId) {
        return goalRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FitnessGoalResponse createGoal(
            Long userId,
            CreateFitnessGoalRequest request
    ) {
        if (goalRepository.existsByUserIdAndStatus(
                userId, GoalStatus.ACTIVE)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already has an active fitness goal"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        FitnessGoal goal = new FitnessGoal();
        goal.setUser(user);
        goal.setGoalType(request.goalType());
        goal.setTargetWeightKg(request.targetWeightKg());
        goal.setTargetDate(request.targetDate());
        goal.setStatus(GoalStatus.ACTIVE);

        return toResponse(goalRepository.save(goal));
    }

    @Transactional
    public FitnessGoalResponse updateGoal(
            Long userId,
            Long goalId,
            UpdateFitnessGoalRequest request
    ) {
        FitnessGoal goal = goalRepository
                .findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fitness goal not found"
                ));

        if (request.status() == GoalStatus.ACTIVE
                && goalRepository.existsByUserIdAndStatusAndIdNot(
                        userId, GoalStatus.ACTIVE, goalId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already has another active fitness goal"
            );
        }

        goal.setGoalType(request.goalType());
        goal.setTargetWeightKg(request.targetWeightKg());
        goal.setTargetDate(request.targetDate());
        goal.setStatus(request.status());

        return toResponse(goalRepository.save(goal));
    }

    private FitnessGoalResponse toResponse(FitnessGoal goal) {
        return new FitnessGoalResponse(
                goal.getId(),
                goal.getGoalType(),
                goal.getTargetWeightKg(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}
