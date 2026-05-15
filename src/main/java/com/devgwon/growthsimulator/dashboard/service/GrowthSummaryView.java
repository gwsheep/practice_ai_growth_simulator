package com.devgwon.growthsimulator.dashboard.service;

import java.util.Comparator;
import java.util.List;

public record GrowthSummaryView(
        int categoryCount,
        int subCategoryCount,
        int totalGrowthExp,
        String topSubCategoryName,
        int topSubCategoryExp,
        String recentGrowthName
) {

    public static GrowthSummaryView from(
            List<GrowthCategorySectionView> categories,
            List<GrowthLogSummaryView> recentLogs
    ) {
        List<GrowthSubCategoryCardView> subCategories = categories.stream()
                .flatMap(category -> category.subCategories().stream())
                .toList();

        GrowthSubCategoryCardView topSubCategory = subCategories.stream()
                .max(Comparator.comparingInt(GrowthSubCategoryCardView::totalExp))
                .orElse(null);

        return new GrowthSummaryView(
                categories.size(),
                subCategories.size(),
                subCategories.stream().mapToInt(GrowthSubCategoryCardView::totalExp).sum(),
                topSubCategory == null ? "아직 없음" : topSubCategory.displayName(),
                topSubCategory == null ? 0 : topSubCategory.totalExp(),
                recentLogs.isEmpty() ? "아직 없음" : recentLogs.get(0).subCategoryDisplayName()
        );
    }
}
