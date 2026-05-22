package com.devgwon.growthsimulator.dto.response;

import java.time.LocalDate;

public record DashboardWeeklyReportSummary(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        int completedQuestCount,
        int totalGainedExp,
        boolean hasWeeklyActivity
) {
}
