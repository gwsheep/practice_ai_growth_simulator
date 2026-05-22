package com.devgwon.growthsimulator.dto.response;

public record WeeklyCategoryGrowthView(
        String categoryDisplayName,
        int expGained,
        int logCount,
        boolean top
) {
}
