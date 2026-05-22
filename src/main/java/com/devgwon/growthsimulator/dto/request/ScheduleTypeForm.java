package com.devgwon.growthsimulator.dto.request;

import com.devgwon.growthsimulator.entity.ScheduleType;

public class ScheduleTypeForm {
    private String name;
    private String code;
    private String description;
    private String color = "#4d78b8";
    private int sortOrder;
    private boolean active = true;

    public static ScheduleTypeForm from(ScheduleType scheduleType) {
        ScheduleTypeForm form = new ScheduleTypeForm();
        form.setName(scheduleType.getName());
        form.setCode(scheduleType.getCode());
        form.setDescription(scheduleType.getDescription());
        form.setColor(scheduleType.getColor());
        form.setSortOrder(scheduleType.getSortOrder());
        form.setActive(scheduleType.isActive());
        return form;
    }

    public String normalizedName() {
        return name == null ? "" : name.trim();
    }

    public String normalizedCode() {
        return code == null ? "" : code.trim().toUpperCase();
    }

    public String normalizedDescription() {
        return description == null ? "" : description.trim();
    }

    public String normalizedColor() {
        return color == null || color.isBlank() ? "#4d78b8" : color.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
