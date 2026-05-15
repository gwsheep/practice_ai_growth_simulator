package com.devgwon.growthsimulator.character.repository;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.domain.DeveloperStat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperStatRepository extends JpaRepository<DeveloperStat, Long> {

    Optional<DeveloperStat> findByProfile(DeveloperProfile profile);
}
