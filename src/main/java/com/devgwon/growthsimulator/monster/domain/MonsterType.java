package com.devgwon.growthsimulator.monster.domain;

public enum MonsterType {
    CAREER_ANXIETY("이직/커리어 불안"),
    SPEC_CHANGE("요구사항 변경"),
    BUG("버그"),
    LEGACY_CODE("레거시 코드"),
    COMMUNICATION("커뮤니케이션"),
    ENVIRONMENT("환경 설정"),
    STUDY_BLOCKER("공부 막힘"),
    ETC("기타");

    private final String label;

    MonsterType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
