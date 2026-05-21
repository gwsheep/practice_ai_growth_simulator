package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.errorrecord.domain.ErrorRecord;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.quest.domain.Quest;
import java.time.LocalDateTime;

public record ErrorRecordView(
        Long id,
        String title,
        String errorName,
        String situation,
        String cause,
        String solution,
        String memo,
        String statusLabel,
        String severityLabel,
        String growthCategoryDisplayName,
        String growthSubCategoryDisplayName,
        Long questId,
        String questTitle,
        LocalDateTime occurredAt,
        LocalDateTime resolvedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean open,
        boolean resolved,
        boolean archived
) {

    public static ErrorRecordView from(ErrorRecord errorRecord) {
        GrowthSubCategory subCategory = errorRecord.getGrowthSubCategory();
        Quest quest = errorRecord.getQuest();
        return new ErrorRecordView(
                errorRecord.getId(),
                errorRecord.getTitle(),
                errorRecord.getErrorName(),
                errorRecord.getSituation(),
                errorRecord.getCause(),
                errorRecord.getSolution(),
                errorRecord.getMemo(),
                errorRecord.getStatus().getLabel(),
                errorRecord.getSeverity().getLabel(),
                subCategory.getCategory().getDisplayName(),
                subCategory.getDisplayName(),
                quest == null ? null : quest.getId(),
                quest == null ? "연결된 Quest 없음" : quest.getTitle(),
                errorRecord.getOccurredAt(),
                errorRecord.getResolvedAt(),
                errorRecord.getCreatedAt(),
                errorRecord.getUpdatedAt(),
                errorRecord.getStatus() == ErrorStatus.OPEN,
                errorRecord.getStatus() == ErrorStatus.RESOLVED,
                errorRecord.getStatus() == ErrorStatus.ARCHIVED
        );
    }
}
