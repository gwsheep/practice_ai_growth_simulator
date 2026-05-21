package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.errorrecord.domain.ErrorSeverity;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import java.util.List;

public record ErrorRecordFormView(
        ErrorRecordForm form,
        boolean editMode,
        Long errorRecordId,
        List<ErrorStatus> statuses,
        List<ErrorSeverity> severities,
        List<GrowthSubCategoryOption> growthSubCategories,
        List<QuestOption> quests
) {
}
