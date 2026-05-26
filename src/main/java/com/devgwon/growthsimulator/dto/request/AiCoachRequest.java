package com.devgwon.growthsimulator.dto.request;

import java.util.List;

public record AiCoachRequest(
        String nickname,
        int level,
        int exp,
        int recentReviewCount,
        String latestReviewSummary,
        int latestMoodScore,
        String latestMoodLabel,
        int completedQuestCount,
        int recentGrowthLogCount,
        int recentGainedExp,
        String topGrowthName,
        List<String> recentQuestTitles,
        List<String> recentGrowthSummaries
) {
}
