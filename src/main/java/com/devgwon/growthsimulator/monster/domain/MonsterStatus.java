package com.devgwon.growthsimulator.monster.domain;

public enum MonsterStatus {
    ACTIVE("전투 중"),
    WEAKENED("약화됨"),
    DEFEATED("처치됨"),
    ARCHIVED("보관됨");

    private final String label;

    MonsterStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
