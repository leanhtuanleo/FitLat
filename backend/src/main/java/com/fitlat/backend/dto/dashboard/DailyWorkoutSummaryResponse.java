package com.fitlat.backend.dto.dashboard;

public record DailyWorkoutSummaryResponse(
        int completedSessionCount,
        int totalDurationMinutes,
        int totalSetCount
) {
}