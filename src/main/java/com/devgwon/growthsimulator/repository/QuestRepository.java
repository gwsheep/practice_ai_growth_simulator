package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.entity.QuestStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    @EntityGraph(attributePaths = {"subCategory", "subCategory.category"})
    List<Quest> findByProfileOrderByCreatedAtDesc(DeveloperProfile profile);

    List<Quest> findTop5ByProfileAndStatusOrderByCompletedAtDesc(DeveloperProfile profile, QuestStatus status);

    List<Quest> findByProfileAndStatusAndCompletedAtBetweenOrderByCompletedAtDesc(
            DeveloperProfile profile,
            QuestStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsBySubCategory(GrowthSubCategory subCategory);
}
