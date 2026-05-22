package com.devgwon.growthsimulator.dto.response;

import com.devgwon.growthsimulator.entity.GrowthSubCategory;

public record GrowthSubCategoryOption(
        Long id,
        String label
) {

    public static GrowthSubCategoryOption from(GrowthSubCategory subCategory) {
        return new GrowthSubCategoryOption(
                subCategory.getId(),
                subCategory.getCategory().getDisplayName() + " · " + subCategory.getDisplayName()
        );
    }
}
