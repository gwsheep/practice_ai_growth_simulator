package com.devgwon.growthsimulator.quest.domain;

public enum QuestStatus {
    READY("진행 전"),
    COMPLETED("완료"),
    CANCELED("취소");

    private final String label;

    QuestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
