package com.devgwon.growthsimulator.entity;

public enum QuestType {
    ALGORITHM("알고리즘"),
    SPRING("스프링"),
    DATABASE("데이터베이스"),
    INFRA("인프라"),
    CS("컴퓨터 사이언스"),
    BLOG("블로그"),
    WORK("업무"),
    CAREER("커리어"),
    REST("휴식");
    private final String label;

    QuestType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
