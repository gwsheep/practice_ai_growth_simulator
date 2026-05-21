package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.quest.domain.Quest;

public record QuestOption(
        Long id,
        String label
) {

    public static QuestOption from(Quest quest) {
        return new QuestOption(
                quest.getId(),
                quest.getTitle() + " · " + quest.getStatus().getLabel()
        );
    }
}
