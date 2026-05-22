package com.devgwon.growthsimulator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class GrowthLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private GrowthSubCategory subCategory;

    @Column(nullable = false)
    private int expGained;

    @Column(nullable = false, length = 500)
    private String message;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected GrowthLog() {
    }

    public GrowthLog(
            DeveloperProfile profile,
            Quest quest,
            GrowthSubCategory subCategory,
            int expGained,
            String message
    ) {
        this.profile = profile;
        this.quest = quest;
        this.subCategory = subCategory;
        this.expGained = expGained;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public Quest getQuest() {
        return quest;
    }

    public GrowthSubCategory getSubCategory() {
        return subCategory;
    }

    public int getExpGained() {
        return expGained;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
