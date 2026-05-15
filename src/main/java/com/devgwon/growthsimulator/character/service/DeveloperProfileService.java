package com.devgwon.growthsimulator.character.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.domain.DeveloperStat;
import com.devgwon.growthsimulator.character.repository.DeveloperProfileRepository;
import com.devgwon.growthsimulator.character.repository.DeveloperStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeveloperProfileService {

    private final DeveloperProfileRepository profileRepository;
    private final DeveloperStatRepository statRepository;

    public DeveloperProfileService(DeveloperProfileRepository profileRepository, DeveloperStatRepository statRepository) {
        this.profileRepository = profileRepository;
        this.statRepository = statRepository;
    }

    @Transactional
    public DeveloperProfile getOrCreateDefaultProfile() {
        return profileRepository.findFirstByOrderByIdAsc()
                .map(this::ensureStat)
                .orElseGet(() -> {
                    DeveloperProfile profile = new DeveloperProfile("devgwon");
                    new DeveloperStat(profile);
                    return profileRepository.save(profile);
                });
    }

    private DeveloperProfile ensureStat(DeveloperProfile profile) {
        if (profile.getStat() == null) {
            DeveloperStat stat = new DeveloperStat(profile);
            statRepository.save(stat);
        }
        return profile;
    }
}
