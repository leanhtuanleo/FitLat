
package com.fitlat.backend.service;

import com.fitlat.backend.dto.CalorieNeedsResponse;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.FitnessGoalRepository;
import com.fitlat.backend.repository.UserProfileRepository;
import com.fitlat.backend.service.calculation.BmrCalculator;
import com.fitlat.backend.service.calculation.TdeeCalculator;
import com.fitlat.backend.service.calculation.CalorieTargetCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;

@Service
public class CalorieCalculationService {

    private final UserProfileRepository userProfileRepository;
    private final FitnessGoalRepository fitnessGoalRepository;

    private final BmrCalculator bmrCalculator = new BmrCalculator();
    private final TdeeCalculator tdeeCalculator = new TdeeCalculator();
    private final CalorieTargetCalculator calorieTargetCalculator =
            new CalorieTargetCalculator();

    public CalorieCalculationService(
            UserProfileRepository userProfileRepository,
            FitnessGoalRepository fitnessGoalRepository
    ) {
        this.userProfileRepository = userProfileRepository;
        this.fitnessGoalRepository = fitnessGoalRepository;
    }

    @Transactional(readOnly = true)
    public CalorieNeedsResponse calculate(Long userId) {

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User profile not found"
                ));

        if (profile.getDateOfBirth() == null
                || profile.getBiologicalSex() == null
                || profile.getHeightCm() == null
                || profile.getCurrentWeightKg() == null
                || profile.getActivityLevel() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please complete your profile before calculating calorie needs"
            );
        }

        int age = Period.between(
                profile.getDateOfBirth(), LocalDate.now()
        ).getYears();

        if (age < 18) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Calorie calculation is currently supported for adults only"
            );
        }

        FitnessGoal activeGoal = fitnessGoalRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Please create an active fitness goal first"
                ));

        int bmr = bmrCalculator.calculate(
                profile.getCurrentWeightKg(),
                profile.getHeightCm(),
                age,
                profile.getBiologicalSex()
        );

        int tdee = tdeeCalculator.calculate(
                bmr, profile.getActivityLevel()
        );

        int dailyCalorieTarget = calorieTargetCalculator.calculate(
                tdee, activeGoal.getGoalType()
        );

        return new CalorieNeedsResponse(
                age, bmr, tdee, dailyCalorieTarget,
                activeGoal.getGoalType()
        );
    }
}
