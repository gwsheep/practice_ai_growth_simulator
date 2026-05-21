package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.errorrecord.domain.ErrorRecord;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorSeverity;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import java.time.LocalDateTime;

public class ErrorRecordForm {

    private String title;
    private String errorName;
    private String situation;
    private String cause;
    private String solution;
    private String memo;
    private ErrorStatus status;
    private ErrorSeverity severity;
    private LocalDateTime occurredAt;
    private Long growthSubCategoryId;
    private Long questId;

    public static ErrorRecordForm createDefault() {
        ErrorRecordForm form = new ErrorRecordForm();
        form.setStatus(ErrorStatus.OPEN);
        form.setSeverity(ErrorSeverity.MEDIUM);
        form.setOccurredAt(LocalDateTime.now());
        return form;
    }

    public static ErrorRecordForm from(ErrorRecord errorRecord) {
        ErrorRecordForm form = new ErrorRecordForm();
        form.setTitle(errorRecord.getTitle());
        form.setErrorName(errorRecord.getErrorName());
        form.setSituation(errorRecord.getSituation());
        form.setCause(errorRecord.getCause());
        form.setSolution(errorRecord.getSolution());
        form.setMemo(errorRecord.getMemo());
        form.setStatus(errorRecord.getStatus());
        form.setSeverity(errorRecord.getSeverity());
        form.setOccurredAt(errorRecord.getOccurredAt());
        form.setGrowthSubCategoryId(errorRecord.getGrowthSubCategory().getId());
        form.setQuestId(errorRecord.getQuest() == null ? null : errorRecord.getQuest().getId());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public ErrorStatus getStatus() {
        return status;
    }

    public void setStatus(ErrorStatus status) {
        this.status = status;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ErrorSeverity severity) {
        this.severity = severity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getGrowthSubCategoryId() {
        return growthSubCategoryId;
    }

    public void setGrowthSubCategoryId(Long growthSubCategoryId) {
        this.growthSubCategoryId = growthSubCategoryId;
    }

    public Long getQuestId() {
        return questId;
    }

    public void setQuestId(Long questId) {
        this.questId = questId;
    }
}
