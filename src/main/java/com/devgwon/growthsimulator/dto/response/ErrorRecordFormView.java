package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.dto.request.ErrorRecordForm;
import com.devgwon.growthsimulator.entity.ErrorSeverity;
import com.devgwon.growthsimulator.entity.ErrorStatus;
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
