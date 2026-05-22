package com.devgwon.growthsimulator.dto.request;

import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterDifficulty;
import com.devgwon.growthsimulator.entity.MonsterType;

public class MonsterUpdateRequest {
    private String name;
    private String code;
    private String description;
    private MonsterType monsterType;
    private MonsterDifficulty difficulty;
    private Integer maxHp;
    private Integer rewardExp;
    private Long relatedGrowthCategoryId;
    private Long relatedGrowthSubCategoryId;

    public static MonsterUpdateRequest from(Monster monster) {
        MonsterUpdateRequest request = new MonsterUpdateRequest();
        request.setName(monster.getName());
        request.setCode(monster.getCode());
        request.setDescription(monster.getDescription());
        request.setMonsterType(monster.getMonsterType());
        request.setDifficulty(monster.getDifficulty());
        request.setMaxHp(monster.getMaxHp());
        request.setRewardExp(monster.getRewardExp());
        request.setRelatedGrowthCategoryId(
                monster.getRelatedGrowthCategory() == null ? null : monster.getRelatedGrowthCategory().getId()
        );
        request.setRelatedGrowthSubCategoryId(
                monster.getRelatedGrowthSubCategory() == null ? null : monster.getRelatedGrowthSubCategory().getId()
        );
        return request;
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

    public MonsterType getMonsterType() {
        return monsterType;
    }

    public void setMonsterType(MonsterType monsterType) {
        this.monsterType = monsterType;
    }

    public MonsterDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(MonsterDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    public Integer getRewardExp() {
        return rewardExp;
    }

    public void setRewardExp(Integer rewardExp) {
        this.rewardExp = rewardExp;
    }

    public Long getRelatedGrowthCategoryId() {
        return relatedGrowthCategoryId;
    }

    public void setRelatedGrowthCategoryId(Long relatedGrowthCategoryId) {
        this.relatedGrowthCategoryId = relatedGrowthCategoryId;
    }

    public Long getRelatedGrowthSubCategoryId() {
        return relatedGrowthSubCategoryId;
    }

    public void setRelatedGrowthSubCategoryId(Long relatedGrowthSubCategoryId) {
        this.relatedGrowthSubCategoryId = relatedGrowthSubCategoryId;
    }
}
