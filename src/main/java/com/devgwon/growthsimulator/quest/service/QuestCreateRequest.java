package com.devgwon.growthsimulator.quest.service;

import com.devgwon.growthsimulator.quest.domain.Difficulty;

public class QuestCreateRequest {

    private String title;
    private String description;
    private Long subCategoryId;
    private Difficulty difficulty = Difficulty.EASY;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
