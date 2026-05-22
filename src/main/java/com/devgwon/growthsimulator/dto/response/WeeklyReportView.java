package com.devgwon.growthsimulator.dto.response;

import java.time.LocalDate;
import java.util.List;

public record WeeklyReportView(
        LocalDate weekStartDate,
        LocalDate weekEndDate,
        LocalDate previousWeekStartDate,
        LocalDate nextWeekStartDate,
        int completedQuestCount,
        int totalGainedExp,
        String mostGrownCategoryName,
        String mostGrownSubCategoryName,
        double averageMoodScore,
        List<WeeklyQuestSummary> completedQuests,
        List<WeeklyCategoryGrowthView> categoryGrowthSummaries,
        List<WeeklySubCategoryGrowthView> subCategoryGrowthSummaries,
        List<WeeklyDailyReviewSummary> dailyReviewSummaries,
        WeeklyMoodSummary moodSummary,
        WeeklyGrowthSummary growthSummary,
        String baekdungiMessage,
        boolean empty
) {
}
