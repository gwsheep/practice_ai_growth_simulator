package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.entity.Schedule;
import java.time.format.DateTimeFormatter;

public record ScheduleView(
        Long id,
        String title,
        String description,
        String typeLabel,
        String statusLabel,
        String startDateTimeText,
        String endDateTimeText,
        String categoryDisplayName,
        String subCategoryDisplayName,
        Long relatedQuestId,
        String relatedQuestTitle,
        boolean hasRelatedQuest,
        boolean planned,
        boolean inProgress,
        boolean done
) {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    public static ScheduleView from(Schedule schedule) {
        GrowthSubCategory subCategory = schedule.getRelatedGrowthSubCategory();
        Quest relatedQuest = schedule.getRelatedQuest();
        return new ScheduleView(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDescription(),
                schedule.getScheduleType().getLabel(),
                schedule.getStatus().getLabel(),
                schedule.getStartDateTime().format(DATE_TIME_FORMATTER),
                schedule.getEndDateTime() == null ? "" : schedule.getEndDateTime().format(DATE_TIME_FORMATTER),
                subCategory == null ? "성장 항목 미지정" : subCategory.getCategory().getDisplayName(),
                subCategory == null ? "미지정" : subCategory.getDisplayName(),
                relatedQuest == null ? null : relatedQuest.getId(),
                relatedQuest == null ? "" : relatedQuest.getTitle(),
                relatedQuest != null,
                schedule.getStatus().name().equals("PLANNED"),
                schedule.getStatus().name().equals("IN_PROGRESS"),
                schedule.getStatus().name().equals("DONE")
        );
    }
}
