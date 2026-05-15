package com.devgwon.growthsimulator.dashboard.service;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import java.util.List;

public record GrowthCategorySectionView(
        Long id,
        String name,
        String displayName,
        String description,
        int sortOrder,
        int subCategoryCount,
        int totalExp,
        int averageLevel,
        List<GrowthSubCategoryCardView> subCategories
) {

    public static GrowthCategorySectionView of(
            GrowthCategory category,
            List<GrowthSubCategoryCardView> subCategories
    ) {
        int totalExp = subCategories.stream()
                .mapToInt(GrowthSubCategoryCardView::totalExp)
                .sum();
        int averageLevel = subCategories.isEmpty()
                ? 0
                : (int) Math.round(subCategories.stream()
                .mapToInt(GrowthSubCategoryCardView::level)
                .average()
                .orElse(0));

        return new GrowthCategorySectionView(
                category.getId(),
                category.getName(),
                category.getDisplayName(),
                category.getDescription(),
                category.getSortOrder(),
                subCategories.size(),
                totalExp,
                averageLevel,
                subCategories
        );
    }
}
