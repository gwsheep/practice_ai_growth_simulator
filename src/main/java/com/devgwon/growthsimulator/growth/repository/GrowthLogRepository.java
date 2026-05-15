package com.devgwon.growthsimulator.growth.repository;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.growth.domain.GrowthLog;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrowthLogRepository extends JpaRepository<GrowthLog, Long> {

    List<GrowthLog> findByProfileOrderByCreatedAtDesc(DeveloperProfile profile);

    List<GrowthLog> findTop5ByProfileOrderByCreatedAtDesc(DeveloperProfile profile);

    List<GrowthLog> findByProfileAndCreatedAtBetweenOrderByCreatedAtDesc(
            DeveloperProfile profile,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsBySubCategory(GrowthSubCategory subCategory);
}
