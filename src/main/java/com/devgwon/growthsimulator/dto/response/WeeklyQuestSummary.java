package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import java.time.LocalDateTime;

public record WeeklyQuestSummary(
        Long id,
        String title,
        String difficultyLabel,
        int expReward,
        String categoryDisplayName,
        String subCategoryDisplayName,
        LocalDateTime completedAt
) {

    public static WeeklyQuestSummary from(Quest quest) {
        GrowthSubCategory subCategory = quest.getSubCategory();
        return new WeeklyQuestSummary(
                quest.getId(),
                quest.getTitle(),
                quest.getDifficulty().getLabel(),
                quest.getExpReward(),
                subCategory == null ? "기존 퀘스트" : subCategory.getCategory().getDisplayName(),
                subCategory == null ? quest.getLegacyQuestType() : subCategory.getDisplayName(),
                quest.getCompletedAt()
        );
    }
}
