package com.devgwon.growthsimulator.common;

public enum BlogDraftType {
    RETROSPECTIVE("회고형"),
    PROBLEM_SOLVING("문제 해결형"),
    LEARNING_LOG("학습 기록형");

    private final String label;

    BlogDraftType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
