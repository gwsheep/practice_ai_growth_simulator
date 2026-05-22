package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.ErrorRecord;
import java.time.LocalDateTime;

public record ErrorRecordDashboardView(
        Long id,
        String title,
        String errorName,
        String statusLabel,
        String severityLabel,
        String growthSubCategoryDisplayName,
        LocalDateTime occurredAt
) {

    public static ErrorRecordDashboardView from(ErrorRecord errorRecord) {
        return new ErrorRecordDashboardView(
                errorRecord.getId(),
                errorRecord.getTitle(),
                errorRecord.getErrorName(),
                errorRecord.getStatus().getLabel(),
                errorRecord.getSeverity().getLabel(),
                errorRecord.getGrowthSubCategory().getDisplayName(),
                errorRecord.getOccurredAt()
        );
    }
}
