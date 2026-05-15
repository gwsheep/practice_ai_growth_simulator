package com.devgwon.growthsimulator.weeklyreport.service;

public record WeeklySubCategoryGrowthView(
        String categoryDisplayName,
        String subCategoryDisplayName,
        int expGained,
        int logCount,
        boolean top
) {
}
