package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.DeveloperProfile;

public record ProfileProgressView(
        String nickname,
        String characterName,
        String title,
        int level,
        int exp,
        int expProgressPercent,
        int expToNextLevel
) {

    public static ProfileProgressView from(DeveloperProfile profile) {
        return new ProfileProgressView(
                profile.getNickname(),
                profile.getCharacterName(),
                profile.getTitle(),
                profile.getLevel(),
                profile.getExp(),
                profile.getExp(),
                100 - profile.getExp()
        );
    }
}
