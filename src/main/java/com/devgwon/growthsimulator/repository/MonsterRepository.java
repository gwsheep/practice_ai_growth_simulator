package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonsterRepository extends JpaRepository<Monster, Long> {

    List<Monster> findByProfileAndStatusNotOrderByCreatedAtDesc(DeveloperProfile profile, MonsterStatus status);

    List<Monster> findByProfileOrderByCreatedAtDesc(DeveloperProfile profile);

    long countByProfileAndStatusIn(DeveloperProfile profile, Collection<MonsterStatus> statuses);

    Optional<Monster> findFirstByProfileAndStatusInOrderByCurrentHpAsc(
            DeveloperProfile profile,
            Collection<MonsterStatus> statuses
    );

    List<Monster> findTop3ByProfileAndStatusInOrderByCurrentHpAsc(
            DeveloperProfile profile,
            Collection<MonsterStatus> statuses
    );

    Optional<Monster> findFirstByProfileAndStatusOrderByDefeatedAtDesc(
            DeveloperProfile profile,
            MonsterStatus status
    );

    boolean existsByProfileAndCode(DeveloperProfile profile, String code);
}
