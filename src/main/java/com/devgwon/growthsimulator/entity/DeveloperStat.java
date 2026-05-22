package com.devgwon.growthsimulator.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class DeveloperStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private DeveloperProfile profile;

    @Column(nullable = false)
    private int algorithm;

    @Column(nullable = false)
    private int spring;

    @Column(nullable = false)
    private int database;

    @Column(nullable = false)
    private int infra;

    @Column(nullable = false)
    private int cs;

    @Column(nullable = false)
    private int communication;

    @Column(nullable = false)
    private int mental;

    @Column(nullable = false)
    private int portfolio;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected DeveloperStat() {
    }

    public DeveloperStat(DeveloperProfile profile) {
        this.profile = profile;
        profile.connectStat(this);
    }

    public void increaseByQuestType(QuestType questType) {
        switch (questType) {
            case ALGORITHM -> algorithm++;
            case SPRING -> spring++;
            case DATABASE -> database++;
            case INFRA -> infra++;
            case CS -> cs++;
            case BLOG -> {
                communication++;
                portfolio++;
            }
            case WORK -> communication++;
            case CAREER -> portfolio++;
            case REST -> mental++;
        }
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public int getSpring() {
        return spring;
    }

    public int getDatabase() {
        return database;
    }

    public int getInfra() {
        return infra;
    }

    public int getCs() {
        return cs;
    }

    public int getCommunication() {
        return communication;
    }

    public int getMental() {
        return mental;
    }

    public int getPortfolio() {
        return portfolio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
