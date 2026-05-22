package com.devgwon.growthsimulator.dto.response;

public class QuestCompleteResult {
    private final Long questId;
    private final String questTitle;
    private final String categoryDisplayName;
    private final String subCategoryDisplayName;
    private final int expReward;
    private final int currentLevel;
    private final int currentExp;
    private final boolean levelUp;
    private final String characterMessage;

    public QuestCompleteResult(
            Long questId,
            String questTitle,
            String categoryDisplayName,
            String subCategoryDisplayName,
            int expReward,
            int currentLevel,
            int currentExp,
            boolean levelUp,
            String characterMessage
    ) {
        this.questId = questId;
        this.questTitle = questTitle;
        this.categoryDisplayName = categoryDisplayName;
        this.subCategoryDisplayName = subCategoryDisplayName;
        this.expReward = expReward;
        this.currentLevel = currentLevel;
        this.currentExp = currentExp;
        this.levelUp = levelUp;
        this.characterMessage = characterMessage;
    }

    public String getSummaryMessage() {
        if (levelUp) {
            return "퀘스트를 완료했습니다. +" + expReward + " EXP, Lv. " + currentLevel + " 달성!";
        }
        return "퀘스트를 완료했습니다. +" + expReward + " EXP";
    }

    public String getGainedStatName() {
        return subCategoryDisplayName;
    }

    public int getGainedStatAmount() {
        return 1;
    }

    public Long getQuestId() {
        return questId;
    }

    public String getQuestTitle() {
        return questTitle;
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public String getSubCategoryDisplayName() {
        return subCategoryDisplayName;
    }

    public int getExpReward() {
        return expReward;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public boolean isLevelUp() {
        return levelUp;
    }

    public String getCharacterMessage() {
        return characterMessage;
    }
}
