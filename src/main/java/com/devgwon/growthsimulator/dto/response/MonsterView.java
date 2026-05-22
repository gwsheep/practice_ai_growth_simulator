package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterStatus;
import java.time.LocalDateTime;

public record MonsterView(
        Long id,
        String name,
        String code,
        String description,
        String typeLabel,
        String statusLabel,
        String difficultyLabel,
        int maxHp,
        int currentHp,
        int hpPercent,
        int rewardExp,
        String relatedCategoryDisplayName,
        String relatedSubCategoryDisplayName,
        boolean attackable,
        boolean defeated,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime defeatedAt
) {

    public static MonsterView from(Monster monster) {
        GrowthCategory category = monster.getRelatedGrowthCategory();
        GrowthSubCategory subCategory = monster.getRelatedGrowthSubCategory();
        int hpPercent = monster.getMaxHp() <= 0
                ? 0
                : Math.max(0, Math.min(100, (int) Math.round(monster.getCurrentHp() * 100.0 / monster.getMaxHp())));
        return new MonsterView(
                monster.getId(),
                monster.getName(),
                monster.getCode(),
                monster.getDescription(),
                monster.getMonsterType().getLabel(),
                monster.getStatus().getLabel(),
                monster.getDifficulty().getLabel(),
                monster.getMaxHp(),
                monster.getCurrentHp(),
                hpPercent,
                monster.getRewardExp(),
                category == null ? "성장 영역 미지정" : category.getDisplayName(),
                subCategory == null ? "미지정" : subCategory.getDisplayName(),
                monster.isAttackable(),
                monster.getStatus() == MonsterStatus.DEFEATED,
                monster.getStatus() == MonsterStatus.ARCHIVED,
                monster.getCreatedAt(),
                monster.getDefeatedAt()
        );
    }
}
