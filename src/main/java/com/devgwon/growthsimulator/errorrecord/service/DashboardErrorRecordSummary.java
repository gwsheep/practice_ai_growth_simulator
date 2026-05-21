package com.devgwon.growthsimulator.errorrecord.service;

import java.util.List;

public record DashboardErrorRecordSummary(
        long openCount,
        long resolvedCount,
        List<ErrorRecordDashboardView> recentErrors
) {
}
