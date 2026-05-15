package com.devgwon.growthsimulator.dashboard.service;

import com.devgwon.growthsimulator.growth.domain.GrowthLog;
import java.time.LocalDateTime;

public record GrowthLogSummaryView(
        Long id,
        String categoryDisplayName,
        String subCategoryDisplayName,
        int expGained,
        String message,
        LocalDateTime createdAt
) {

    public static GrowthLogSummaryView from(GrowthLog log) {
        return new GrowthLogSummaryView(
                log.getId(),
                log.getSubCategory().getCategory().getDisplayName(),
                log.getSubCategory().getDisplayName(),
                log.getExpGained(),
                log.getMessage(),
                log.getCreatedAt()
        );
    }
}
