package com.devgwon.growthsimulator.errorrecord.domain;

public enum ErrorSeverity {
    LOW("가벼움"),
    MEDIUM("주의"),
    HIGH("높음"),
    CRITICAL("치명적");

    private final String label;

    ErrorSeverity(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
