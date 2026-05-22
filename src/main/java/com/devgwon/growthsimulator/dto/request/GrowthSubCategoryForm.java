package com.devgwon.growthsimulator.dto.request;

import com.devgwon.growthsimulator.entity.GrowthSubCategory;

public class GrowthSubCategoryForm {
    private Long categoryId;
    private String name;
    private String displayName;
    private String description;
    private Integer sortOrder = 0;

    public static GrowthSubCategoryForm from(GrowthSubCategory subCategory) {
        GrowthSubCategoryForm form = new GrowthSubCategoryForm();
        form.setCategoryId(subCategory.getCategory().getId());
        form.setName(subCategory.getName());
        form.setDisplayName(subCategory.getDisplayName());
        form.setDescription(subCategory.getDescription());
        form.setSortOrder(subCategory.getSortOrder());
        return form;
    }

    public int normalizedSortOrder() {
        return sortOrder == null ? 0 : sortOrder;
    }

    public String normalizedName() {
        return name == null ? null : name.trim().toUpperCase();
    }

    public String normalizedDisplayName() {
        return displayName == null ? null : displayName.trim();
    }

    public String normalizedDescription() {
        return description == null ? "" : description.trim();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
