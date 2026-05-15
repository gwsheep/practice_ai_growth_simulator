package com.devgwon.growthsimulator.dashboard.service;

import com.devgwon.growthsimulator.schedule.service.DashboardScheduleSummary;
import com.devgwon.growthsimulator.review.service.DailyReviewSummaryView;
import com.devgwon.growthsimulator.weeklyreport.service.DashboardWeeklyReportSummary;
import com.devgwon.growthsimulator.monster.service.DashboardMonsterSummary;
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
        DashboardMonsterSummary monsterSummary
) {
}
