package com.devgwon.growthsimulator.schedule.domain;

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
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_sub_category_id")
    private GrowthSubCategory relatedGrowthSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_quest_id")
    private Quest relatedQuest;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_type_id", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "schedule_type", nullable = false, length = 50)
    private String legacyScheduleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScheduleStatus status;

    private LocalDateTime completedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Schedule() {
    }

    public Schedule(
            DeveloperProfile profile,
            GrowthSubCategory relatedGrowthSubCategory,
            String title,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            ScheduleType scheduleType
    ) {
        this.profile = profile;
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.scheduleType = scheduleType;
        this.legacyScheduleType = scheduleType.getCode();
        this.status = ScheduleStatus.PLANNED;
    }

    public void update(
            GrowthSubCategory relatedGrowthSubCategory,
            String title,
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            ScheduleType scheduleType,
            ScheduleStatus status
    ) {
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.scheduleType = scheduleType;
        this.legacyScheduleType = scheduleType.getCode();
        this.status = status;
        this.completedAt = status == ScheduleStatus.DONE
                ? (this.completedAt == null ? LocalDateTime.now() : this.completedAt)
                : null;
    }

    public void done() {
        this.status = ScheduleStatus.DONE;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ScheduleStatus.CANCELED;
        this.completedAt = null;
    }

    public void linkQuest(Quest relatedQuest) {
        this.relatedQuest = relatedQuest;
    }

    public boolean hasRelatedQuest() {
        return relatedQuest != null;
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public GrowthSubCategory getRelatedGrowthSubCategory() {
        return relatedGrowthSubCategory;
    }

    public Quest getRelatedQuest() {
        return relatedQuest;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public String getLegacyScheduleType() {
        return legacyScheduleType;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
