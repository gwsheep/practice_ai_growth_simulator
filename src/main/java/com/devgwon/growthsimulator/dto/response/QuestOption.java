package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.Quest;

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
