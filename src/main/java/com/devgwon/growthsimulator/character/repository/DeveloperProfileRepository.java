package com.devgwon.growthsimulator.character.repository;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {

    Optional<DeveloperProfile> findFirstByOrderByIdAsc();
}
