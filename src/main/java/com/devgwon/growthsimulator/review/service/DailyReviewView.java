package com.devgwon.growthsimulator.review.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import java.time.format.DateTimeFormatter;

public record DailyReviewView(
        Long id,
        String reviewDateText,
        String learnedText,
        String difficultyText,
        String tomorrowPlanText,
        int moodScore,
        String moodLabel,
        String categoryDisplayName,
        String subCategoryDisplayName
) {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static DailyReviewView from(DailyReview review) {
        GrowthSubCategory subCategory = review.getRelatedGrowthSubCategory();
        return new DailyReviewView(
                review.getId(),
                review.getReviewDate().format(DATE_FORMATTER),
                review.getLearnedText(),
                review.getDifficultyText(),
                review.getTomorrowPlanText(),
                review.getMoodScore(),
                moodLabel(review.getMoodScore()),
                subCategory == null ? "성장 항목 미지정" : subCategory.getCategory().getDisplayName(),
                subCategory == null ? "미지정" : subCategory.getDisplayName()
        );
    }

    public static String moodLabel(int moodScore) {
        return switch (moodScore) {
            case 1 -> "매우 힘듦";
            case 2 -> "힘듦";
            case 4 -> "좋음";
            case 5 -> "아주 좋음";
            default -> "보통";
        };
    }
}
