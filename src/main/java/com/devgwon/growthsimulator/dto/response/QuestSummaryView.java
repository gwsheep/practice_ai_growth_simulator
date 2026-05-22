package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.Quest;

public record QuestSummaryView(
        Long id,
        String title,
        String description,
        String categoryDisplayName,
        String subCategoryDisplayName,
        String difficultyLabel,
        String statusLabel,
        int expReward,
        boolean ready
) {

    public static QuestSummaryView from(Quest quest) {
        String categoryDisplayName = quest.getSubCategory() == null
                ? "기존 퀘스트"
                : quest.getSubCategory().getCategory().getDisplayName();
        String subCategoryDisplayName = quest.getSubCategory() == null
                ? quest.getLegacyQuestType()
                : quest.getSubCategory().getDisplayName();
        return new QuestSummaryView(
                quest.getId(),
                quest.getTitle(),
                quest.getDescription(),
                categoryDisplayName,
                subCategoryDisplayName,
                quest.getDifficulty().getLabel(),
                quest.getStatus().getLabel(),
                quest.getExpReward(),
                quest.getStatus().name().equals("READY")
        );
    }
}
