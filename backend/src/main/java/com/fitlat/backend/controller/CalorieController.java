
package com.fitlat.backend.controller;

import com.fitlat.backend.dto.CalorieNeedsResponse;
import com.fitlat.backend.service.CalorieCalculationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class CalorieController {

    private final CalorieCalculationService calorieCalculationService;

    public CalorieController(
            CalorieCalculationService calorieCalculationService
    ) {
        this.calorieCalculationService = calorieCalculationService;
    }

    @GetMapping("/calorie-needs")
    public CalorieNeedsResponse getCalorieNeeds(
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());

        return calorieCalculationService.calculate(userId);
    }
}
