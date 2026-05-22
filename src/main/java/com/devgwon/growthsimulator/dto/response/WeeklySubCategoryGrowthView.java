package com.devgwon.growthsimulator.dto.response;

public record WeeklySubCategoryGrowthView(
        String categoryDisplayName,
        String subCategoryDisplayName,
        int expGained,
        int logCount,
        boolean top
) {
}
