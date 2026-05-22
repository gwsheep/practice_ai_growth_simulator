package com.devgwon.growthsimulator.dto.response;

import java.util.List;

public record DashboardView(
        ProfileProgressView profile,
        GrowthSummaryView growthSummary,
        List<GrowthCategorySectionView> growthCategories,
        List<GrowthLogSummaryView> recentGrowthLogs,
        List<QuestSummaryView> quests,
        List<QuestSummaryView> recentCompletedQuests,
        long todayScheduleCount,
        long weekScheduleCount,
        List<DashboardScheduleSummary> todaySchedules,
        boolean todayReviewWritten,
        Long todayReviewId,
        List<DailyReviewSummaryView> recentDailyReviews,
        DashboardWeeklyReportSummary weeklyReportSummary,
        DashboardMonsterSummary monsterSummary,
        DashboardErrorRecordSummary errorRecordSummary
) {
}
