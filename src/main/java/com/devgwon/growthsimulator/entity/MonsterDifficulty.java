package com.devgwon.growthsimulator.entity;

public enum MonsterDifficulty {
    EASY("쉬움", 30),
    NORMAL("보통", 60),
    HARD("어려움", 100),
    BOSS("보스", 200);
    private final String label;
    private final int defaultMaxHp;

    MonsterDifficulty(String label, int defaultMaxHp) {
        this.label = label;
        this.defaultMaxHp = defaultMaxHp;
    }

    public String getLabel() {
        return label;
    }

    public int getDefaultMaxHp() {
        return defaultMaxHp;
    }
}
