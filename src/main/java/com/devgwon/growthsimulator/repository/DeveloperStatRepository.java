package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.DeveloperStat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperStatRepository extends JpaRepository<DeveloperStat, Long> {

    Optional<DeveloperStat> findByProfile(DeveloperProfile profile);
}
