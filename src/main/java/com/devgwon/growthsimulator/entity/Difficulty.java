package com.devgwon.growthsimulator.entity;

public enum Difficulty {
    EASY("쉬움", 10),
    NORMAL("보통", 30),
    HARD("어려움", 60),
    BOSS("보스", 100);
    private final String label;
    private final int expReward;

    Difficulty(String label, int expReward) {
        this.label = label;
        this.expReward = expReward;
    }

    public String getLabel() {
        return label;
    }

    public int getExpReward() {
        return expReward;
    }
}
