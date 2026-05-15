package com.devgwon.growthsimulator.monster.service;

public record MonsterAttackResult(
        Long monsterId,
        String monsterName,
        int damage,
        int currentHp,
        int maxHp,
        boolean defeated,
        String message
) {
}
