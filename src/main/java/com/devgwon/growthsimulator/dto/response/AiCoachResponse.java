package com.devgwon.growthsimulator.dto.response;

import java.util.List;

public record AiCoachResponse(
        String coachMessage,
        String encouragementMessage,
        String growthSummary,
        String reviewSummary,
        List<String> recommendedActions,
        boolean fallback
) {
}
