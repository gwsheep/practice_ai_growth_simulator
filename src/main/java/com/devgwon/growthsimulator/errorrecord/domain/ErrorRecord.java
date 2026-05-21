package com.devgwon.growthsimulator.errorrecord.domain;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.quest.domain.Quest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ErrorRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 200)
    private String errorName;

    @Column(nullable = false, length = 2000)
    private String situation;

    @Column(length = 2000)
    private String cause;

    @Column(length = 3000)
    private String solution;

    @Column(length = 2000)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ErrorStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ErrorSeverity severity;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "growth_sub_category_id", nullable = false)
    private GrowthSubCategory growthSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected ErrorRecord() {
    }

    public ErrorRecord(
            DeveloperProfile profile,
            String title,
            String errorName,
            String situation,
            String cause,
            String solution,
            String memo,
            ErrorStatus status,
            ErrorSeverity severity,
            LocalDateTime occurredAt,
            GrowthSubCategory growthSubCategory,
            Quest quest
    ) {
        this.profile = profile;
        this.title = title;
        this.errorName = errorName;
        this.situation = situation;
        this.cause = cause;
        this.solution = solution;
        this.memo = memo;
        this.status = status;
        this.severity = severity;
        this.occurredAt = occurredAt;
        this.growthSubCategory = growthSubCategory;
        this.quest = quest;
        if (status == ErrorStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public void update(
            String title,
            String errorName,
            String situation,
            String cause,
            String solution,
            String memo,
            ErrorStatus status,
            ErrorSeverity severity,
            LocalDateTime occurredAt,
            GrowthSubCategory growthSubCategory,
            Quest quest
    ) {
        this.title = title;
        this.errorName = errorName;
        this.situation = situation;
        this.cause = cause;
        this.solution = solution;
        this.memo = memo;
        this.status = status;
        this.severity = severity;
        this.occurredAt = occurredAt;
        this.growthSubCategory = growthSubCategory;
        this.quest = quest;
        if (status == ErrorStatus.RESOLVED && resolvedAt == null) {
            resolvedAt = LocalDateTime.now();
        }
        if (status == ErrorStatus.OPEN) {
            resolvedAt = null;
        }
    }

    public void resolve() {
        this.status = ErrorStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void archive() {
        this.status = ErrorStatus.ARCHIVED;
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public String getTitle() {
        return title;
    }

    public String getErrorName() {
        return errorName;
    }

    public String getSituation() {
        return situation;
    }

    public String getCause() {
        return cause;
    }

    public String getSolution() {
        return solution;
    }

    public String getMemo() {
        return memo;
    }

    public ErrorStatus getStatus() {
        return status;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public GrowthSubCategory getGrowthSubCategory() {
        return growthSubCategory;
    }

    public Quest getQuest() {
        return quest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
