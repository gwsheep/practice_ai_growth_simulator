package com.devgwon.growthsimulator.growth.web;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import java.util.List;

public record GrowthCategoryView(
        GrowthCategory category,
        List<GrowthSubCategory> subCategories
) {
}
