package com.devgwon.growthsimulator.schedule.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;

public class ScheduleCreateRequest {

    private String title;
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    private Integer startHour = 19;
    private Integer startMinute = 0;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer endHour = 20;
    private Integer endMinute = 0;

    private Long scheduleTypeId;
    private Long relatedGrowthSubCategoryId;

    public LocalDateTime toStartDateTime() {
        if (startDate == null || startHour == null || startMinute == null) {
            return null;
        }
        return LocalDateTime.of(startDate, LocalTime.of(startHour, startMinute));
    }

    public LocalDateTime toEndDateTime() {
        if (endDate == null) {
            return null;
        }
        return LocalDateTime.of(endDate, LocalTime.of(endHour == null ? 0 : endHour, endMinute == null ? 0 : endMinute));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public Long getScheduleTypeId() {
        return scheduleTypeId;
    }

    public void setScheduleTypeId(Long scheduleTypeId) {
        this.scheduleTypeId = scheduleTypeId;
    }

    public Long getRelatedGrowthSubCategoryId() {
        return relatedGrowthSubCategoryId;
    }

    public void setRelatedGrowthSubCategoryId(Long relatedGrowthSubCategoryId) {
        this.relatedGrowthSubCategoryId = relatedGrowthSubCategoryId;
    }
}
