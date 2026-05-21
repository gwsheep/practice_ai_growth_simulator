package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;

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
