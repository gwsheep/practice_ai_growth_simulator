package com.devgwon.growthsimulator.weeklyreport.service;

public record WeeklyCategoryGrowthView(
        String categoryDisplayName,
        int expGained,
        int logCount,
        boolean top
) {
}
