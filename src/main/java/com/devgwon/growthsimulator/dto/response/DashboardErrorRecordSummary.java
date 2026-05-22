package com.devgwon.growthsimulator.dto.response;

import java.util.List;

public record DashboardErrorRecordSummary(
        long openCount,
        long resolvedCount,
        List<ErrorRecordDashboardView> recentErrors
) {
}
