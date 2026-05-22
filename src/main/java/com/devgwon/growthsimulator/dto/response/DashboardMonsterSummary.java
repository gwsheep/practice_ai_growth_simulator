package com.devgwon.growthsimulator.dto.response;

import java.util.List;

public record DashboardMonsterSummary(
        long activeMonsterCount,
        MonsterView almostDefeatedMonster,
        MonsterView recentDefeatedMonster,
        List<MonsterView> activeMonsters
) {
}
