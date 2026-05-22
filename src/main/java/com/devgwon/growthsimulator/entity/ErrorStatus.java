package com.devgwon.growthsimulator.entity;

public enum ErrorStatus {
    OPEN("수집 중"),
    RESOLVED("해결됨"),
    ARCHIVED("보관됨");
    private final String label;

    ErrorStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
