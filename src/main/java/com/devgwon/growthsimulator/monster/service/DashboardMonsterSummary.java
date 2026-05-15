package com.devgwon.growthsimulator.monster.service;

public record DashboardMonsterSummary(
        long activeMonsterCount,
        MonsterView almostDefeatedMonster,
        MonsterView recentDefeatedMonster
) {
}
