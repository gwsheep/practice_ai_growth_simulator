package com.devgwon.growthsimulator.dto.response;

public record WeeklyGrowthSummary(
        int totalGainedExp,
        int growthLogCount,
        String mostGrownCategoryName,
        String mostGrownSubCategoryName
) {
}
