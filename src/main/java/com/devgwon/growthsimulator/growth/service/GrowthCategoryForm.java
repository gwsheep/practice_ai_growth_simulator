package com.devgwon.growthsimulator.growth.service;

import com.devgwon.growthsimulator.growth.domain.GrowthCategory;

public class GrowthCategoryForm {

    private String name;
    private String displayName;
    private String description;
    private Integer sortOrder = 0;

    public static GrowthCategoryForm from(GrowthCategory category) {
        GrowthCategoryForm form = new GrowthCategoryForm();
        form.setName(category.getName());
        form.setDisplayName(category.getDisplayName());
        form.setDescription(category.getDescription());
        form.setSortOrder(category.getSortOrder());
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
