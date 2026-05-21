package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.errorrecord.domain.ErrorRecord;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.quest.domain.Quest;
import java.time.LocalDateTime;

public record ErrorRecordListItemView(
        Long id,
        String title,
        String errorName,
        String situation,
        String statusLabel,
        String severityLabel,
        String growthCategoryDisplayName,
        String growthSubCategoryDisplayName,
        Long questId,
        String questTitle,
        LocalDateTime occurredAt,
        boolean open,
        boolean archived
) {

    public static ErrorRecordListItemView from(ErrorRecord errorRecord) {
        GrowthSubCategory subCategory = errorRecord.getGrowthSubCategory();
        Quest quest = errorRecord.getQuest();
        return new ErrorRecordListItemView(
                errorRecord.getId(),
                errorRecord.getTitle(),
                errorRecord.getErrorName(),
                errorRecord.getSituation(),
                errorRecord.getStatus().getLabel(),
                errorRecord.getSeverity().getLabel(),
                subCategory.getCategory().getDisplayName(),
                subCategory.getDisplayName(),
                quest == null ? null : quest.getId(),
                quest == null ? "연결된 Quest 없음" : quest.getTitle(),
                errorRecord.getOccurredAt(),
                errorRecord.getStatus() == ErrorStatus.OPEN,
                errorRecord.getStatus() == ErrorStatus.ARCHIVED
        );
    }
}
