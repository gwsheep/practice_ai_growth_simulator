package com.devgwon.growthsimulator.weeklyreport.service;

public record WeeklyGrowthSummary(
        int totalGainedExp,
        int growthLogCount,
        String mostGrownCategoryName,
        String mostGrownSubCategoryName
) {
}
