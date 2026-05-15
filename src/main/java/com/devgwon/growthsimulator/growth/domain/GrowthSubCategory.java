package com.devgwon.growthsimulator.growth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_growth_sub_category_category_name", columnNames = {"category_id", "name"})
})
public class GrowthSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private GrowthCategory category;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private int totalExp;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private int sortOrder;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected GrowthSubCategory() {
    }

    public GrowthSubCategory(
            GrowthCategory category,
            String name,
            String displayName,
            String description,
            int sortOrder
    ) {
        this.category = category;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.totalExp = 0;
        this.level = 1;
        this.sortOrder = sortOrder;
    }

    public void addExp(int exp) {
        this.totalExp += exp;
        this.level = (this.totalExp / 100) + 1;
    }

    public void update(GrowthCategory category, String name, String displayName, String description, int sortOrder) {
        this.category = category;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public GrowthCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int getLevel() {
        return level;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
