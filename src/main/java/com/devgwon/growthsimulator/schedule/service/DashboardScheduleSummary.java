package com.devgwon.growthsimulator.schedule.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.schedule.domain.Schedule;
import java.time.format.DateTimeFormatter;

public record DashboardScheduleSummary(
        Long id,
        String title,
        String typeLabel,
        String statusLabel,
        String timeText,
        String subCategoryDisplayName
) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static DashboardScheduleSummary from(Schedule schedule) {
        GrowthSubCategory subCategory = schedule.getRelatedGrowthSubCategory();
        return new DashboardScheduleSummary(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getScheduleType().getLabel(),
                schedule.getStatus().getLabel(),
                schedule.getStartDateTime().format(TIME_FORMATTER),
                subCategory == null ? "미지정" : subCategory.getDisplayName()
        );
    }
}
