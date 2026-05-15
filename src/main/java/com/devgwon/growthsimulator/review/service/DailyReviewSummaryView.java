package com.devgwon.growthsimulator.review.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import java.time.format.DateTimeFormatter;

public record DailyReviewSummaryView(
        Long id,
        String reviewDateText,
        int moodScore,
        String moodLabel,
        String learnedSummary,
        String subCategoryDisplayName
) {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");

    public static DailyReviewSummaryView from(DailyReview review) {
        GrowthSubCategory subCategory = review.getRelatedGrowthSubCategory();
        return new DailyReviewSummaryView(
                review.getId(),
                review.getReviewDate().format(DATE_FORMATTER),
                review.getMoodScore(),
                DailyReviewView.moodLabel(review.getMoodScore()),
                summarize(review.getLearnedText()),
                subCategory == null ? "미지정" : subCategory.getDisplayName()
        );
    }

    private static String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "배운 내용을 짧게 남겨보세요.";
        }
        String normalized = text.strip();
        return normalized.length() <= 40 ? normalized : normalized.substring(0, 40) + "...";
    }
}
