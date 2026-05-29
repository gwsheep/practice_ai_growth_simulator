package com.devgwon.growthsimulator.common;

public enum BlogDraftSourceType {
    DAILY_REVIEW("Daily Review"),
    ERROR_RECORD("Error Record"),
    GROWTH_LOG("GrowthLog");

    private final String label;

    BlogDraftSourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
