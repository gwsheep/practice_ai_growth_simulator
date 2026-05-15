package com.devgwon.growthsimulator.weeklyreport.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.service.DailyReviewView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record WeeklyDailyReviewSummary(
        Long id,
        LocalDate reviewDate,
        String reviewDateText,
        int moodScore,
        String moodLabel,
        String learnedSummary,
        String difficultySummary,
        String tomorrowPlanSummary,
        String subCategoryDisplayName
) {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM.dd");

    public static WeeklyDailyReviewSummary from(DailyReview review) {
        GrowthSubCategory subCategory = review.getRelatedGrowthSubCategory();
        return new WeeklyDailyReviewSummary(
                review.getId(),
                review.getReviewDate(),
                review.getReviewDate().format(DATE_FORMATTER),
                review.getMoodScore(),
                DailyReviewView.moodLabel(review.getMoodScore()),
                summarize(review.getLearnedText()),
                summarize(review.getDifficultyText()),
                summarize(review.getTomorrowPlanText()),
                subCategory == null ? "미지정" : subCategory.getDisplayName()
        );
    }

    private static String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "기록 없음";
        }
        String normalized = text.strip();
        return normalized.length() <= 48 ? normalized : normalized.substring(0, 48) + "...";
    }
}
