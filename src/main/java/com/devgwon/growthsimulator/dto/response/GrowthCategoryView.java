package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import java.util.List;

public record GrowthCategoryView(
        GrowthCategory category,
        List<GrowthSubCategory> subCategories
) {
}
