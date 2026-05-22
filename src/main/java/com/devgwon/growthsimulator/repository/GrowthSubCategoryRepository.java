package com.devgwon.growthsimulator.repository;

import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrowthSubCategoryRepository extends JpaRepository<GrowthSubCategory, Long> {

    Optional<GrowthSubCategory> findByCategoryAndName(GrowthCategory category, String name);

    Optional<GrowthSubCategory> findByCategory_NameAndName(String categoryName, String name);

    long countByCategory(GrowthCategory category);

    List<GrowthSubCategory> findByCategoryOrderBySortOrderAsc(GrowthCategory category);

    @EntityGraph(attributePaths = "category")
    List<GrowthSubCategory> findAllByOrderByCategory_SortOrderAscSortOrderAsc();
}
