package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthSubCategory;

public record GrowthSubCategoryCardView(
        Long id,
        String name,
        String displayName,
        String description,
        int level,
        int totalExp,
        int currentLevelExp,
        int progressPercent,
        int expToNextLevel,
        int sortOrder
) {

    public static GrowthSubCategoryCardView from(GrowthSubCategory subCategory) {
        int currentLevelExp = subCategory.getTotalExp() % 100;
        return new GrowthSubCategoryCardView(
                subCategory.getId(),
                subCategory.getName(),
                subCategory.getDisplayName(),
                subCategory.getDescription(),
                subCategory.getLevel(),
                subCategory.getTotalExp(),
                currentLevelExp,
                currentLevelExp,
                100 - currentLevelExp,
                subCategory.getSortOrder()
        );
    }
}
