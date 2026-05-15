package com.devgwon.growthsimulator.schedule.domain;

public enum ScheduleStatus {
    PLANNED("예정"),
    IN_PROGRESS("진행 중"),
    DONE("완료"),
    CANCELED("취소");

    private final String label;

    ScheduleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
