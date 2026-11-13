
package com.fitlat.backend.controller;

import com.fitlat.backend.dto.dashboard.DailyDashboardResponse;
import com.fitlat.backend.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users/me/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/daily")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public DailyDashboardResponse getDailyDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long userId = Long.parseLong(authentication.getName());

        LocalDate targetDate = date != null ? date : LocalDate.now();

        return dashboardService.getDailyDashboard(userId, targetDate);
    }
}
