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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_monster_profile_code", columnNames = {"profile_id", "code"})
})
public class Monster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MonsterType monsterType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MonsterStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MonsterDifficulty difficulty;

    @Column(nullable = false)
    private int maxHp;

    @Column(nullable = false)
    private int currentHp;

    @Column(nullable = false)
    private int rewardExp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_growth_category_id")
    private GrowthCategory relatedGrowthCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_growth_sub_category_id")
    private GrowthSubCategory relatedGrowthSubCategory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime defeatedAt;

    protected Monster() {
    }

    public Monster(
            DeveloperProfile profile,
            String name,
            String code,
            String description,
            MonsterType monsterType,
            MonsterDifficulty difficulty,
            int maxHp,
            int rewardExp,
            GrowthCategory relatedGrowthCategory,
            GrowthSubCategory relatedGrowthSubCategory
    ) {
        this.profile = profile;
        this.name = name;
        this.code = code;
        this.description = description;
        this.monsterType = monsterType;
        this.status = MonsterStatus.ACTIVE;
        this.difficulty = difficulty;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.rewardExp = rewardExp;
        this.relatedGrowthCategory = relatedGrowthCategory;
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
    }

    public void update(
            String name,
            String code,
            String description,
            MonsterType monsterType,
            MonsterDifficulty difficulty,
            int maxHp,
            int rewardExp,
            GrowthCategory relatedGrowthCategory,
            GrowthSubCategory relatedGrowthSubCategory
    ) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.monsterType = monsterType;
        this.difficulty = difficulty;
        this.maxHp = maxHp;
        this.currentHp = Math.min(this.currentHp, maxHp);
        this.rewardExp = rewardExp;
        this.relatedGrowthCategory = relatedGrowthCategory;
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
        refreshBattleStatus();
    }

    public void attack(int damage) {
        if (!isAttackable()) {
            throw new IllegalStateException("이미 종료된 몬스터는 공격할 수 없습니다.");
        }
        if (damage <= 0) {
            throw new IllegalArgumentException("공격력은 1 이상이어야 합니다.");
        }
        this.currentHp = Math.max(0, this.currentHp - damage);
        refreshBattleStatus();
    }

    public void archive() {
        this.status = MonsterStatus.ARCHIVED;
    }

    public boolean isAttackable() {
        return status != MonsterStatus.DEFEATED && status != MonsterStatus.ARCHIVED;
    }

    private void refreshBattleStatus() {
        if (currentHp <= 0) {
            currentHp = 0;
            status = MonsterStatus.DEFEATED;
            defeatedAt = defeatedAt == null ? LocalDateTime.now() : defeatedAt;
            return;
        }
        defeatedAt = null;
        if (currentHp <= Math.ceil(maxHp * 0.3)) {
            status = MonsterStatus.WEAKENED;
            return;
        }
        status = MonsterStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public MonsterStatus getStatus() {
        return status;
    }

    public MonsterDifficulty getDifficulty() {
        return difficulty;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public GrowthCategory getRelatedGrowthCategory() {
        return relatedGrowthCategory;
    }

    public GrowthSubCategory getRelatedGrowthSubCategory() {
        return relatedGrowthSubCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDefeatedAt() {
        return defeatedAt;
    }
}
