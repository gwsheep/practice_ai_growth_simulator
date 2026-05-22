package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.DeveloperStat;
import com.devgwon.growthsimulator.repository.DeveloperProfileRepository;
import com.devgwon.growthsimulator.repository.DeveloperStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeveloperProfileService {
    private final DeveloperProfileRepository profileRepository;
    private final DeveloperStatRepository statRepository;

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
