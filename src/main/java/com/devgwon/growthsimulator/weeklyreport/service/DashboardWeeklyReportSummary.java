package com.devgwon.growthsimulator.weeklyreport.service;

import java.time.LocalDate;

public record DashboardWeeklyReportSummary(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        int completedQuestCount,
        int totalGainedExp,
        boolean hasWeeklyActivity
) {
}
