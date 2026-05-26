package com.devgwon.growthsimulator.dto.response;

import java.util.List;

public record AiCoachView(
        String coachMessage,
        String encouragementMessage,
        String growthSummary,
        String reviewSummary,
        List<String> recommendedActions,
        boolean fallback,
        boolean empty
) {
    public static AiCoachView from(AiCoachResponse response, boolean empty) {
        return new AiCoachView(
                response.coachMessage(),
                response.encouragementMessage(),
                response.growthSummary(),
                response.reviewSummary(),
                response.recommendedActions(),
                response.fallback(),
                empty
        );
    }
}
