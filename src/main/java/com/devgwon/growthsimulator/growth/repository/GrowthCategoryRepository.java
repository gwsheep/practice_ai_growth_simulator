package com.devgwon.growthsimulator.growth.repository;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrowthCategoryRepository extends JpaRepository<GrowthCategory, Long> {

    Optional<GrowthCategory> findByName(String name);

    List<GrowthCategory> findAllByOrderBySortOrderAsc();
}
