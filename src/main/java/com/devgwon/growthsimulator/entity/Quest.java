package com.devgwon.growthsimulator.entity;

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
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id")
    private GrowthSubCategory subCategory;

    @Column(name = "quest_type", nullable = false)
    private String legacyQuestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestStatus status;

    @Column(nullable = false)
    private int expReward;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Quest() {
    }

    public Quest(
            DeveloperProfile profile,
            GrowthSubCategory subCategory,
            String title,
            String description,
            Difficulty difficulty
    ) {
        this.profile = profile;
        this.subCategory = subCategory;
        this.title = title;
        this.description = description;
        this.legacyQuestType = "WORK";
        this.difficulty = difficulty;
        this.status = QuestStatus.READY;
        this.expReward = difficulty.getExpReward();
    }

    public void complete() {
        this.status = QuestStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == QuestStatus.COMPLETED;
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

    public String getDescription() {
        return description;
    }

    public GrowthSubCategory getSubCategory() {
        return subCategory;
    }

    public String getLegacyQuestType() {
        return legacyQuestType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public int getExpReward() {
        return expReward;
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
