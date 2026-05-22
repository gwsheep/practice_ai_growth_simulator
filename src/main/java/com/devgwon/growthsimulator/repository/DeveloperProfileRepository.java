package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {

    Optional<DeveloperProfile> findFirstByOrderByIdAsc();
}
