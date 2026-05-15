package com.devgwon.growthsimulator.review.domain;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_review_profile_date", columnNames = {"profile_id", "review_date"})
})
public class DailyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private DeveloperProfile profile;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Column(nullable = false, length = 1500)
    private String learnedText;

    @Column(nullable = false, length = 1500)
    private String difficultyText;

    @Column(nullable = false, length = 1500)
    private String tomorrowPlanText;

    @Column(nullable = false)
    private int moodScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_sub_category_id")
    private GrowthSubCategory relatedGrowthSubCategory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected DailyReview() {
    }

    public DailyReview(
            DeveloperProfile profile,
            LocalDate reviewDate,
            String learnedText,
            String difficultyText,
            String tomorrowPlanText,
            int moodScore,
            GrowthSubCategory relatedGrowthSubCategory
    ) {
        this.profile = profile;
        this.reviewDate = reviewDate;
        this.learnedText = learnedText;
        this.difficultyText = difficultyText;
        this.tomorrowPlanText = tomorrowPlanText;
        this.moodScore = moodScore;
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
    }

    public void update(
            LocalDate reviewDate,
            String learnedText,
            String difficultyText,
            String tomorrowPlanText,
            int moodScore,
            GrowthSubCategory relatedGrowthSubCategory
    ) {
        this.reviewDate = reviewDate;
        this.learnedText = learnedText;
        this.difficultyText = difficultyText;
        this.tomorrowPlanText = tomorrowPlanText;
        this.moodScore = moodScore;
        this.relatedGrowthSubCategory = relatedGrowthSubCategory;
    }

    public Long getId() {
        return id;
    }

    public DeveloperProfile getProfile() {
        return profile;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public String getLearnedText() {
        return learnedText;
    }

    public String getDifficultyText() {
        return difficultyText;
    }

    public String getTomorrowPlanText() {
        return tomorrowPlanText;
    }

    public int getMoodScore() {
        return moodScore;
    }

    public GrowthSubCategory getRelatedGrowthSubCategory() {
        return relatedGrowthSubCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
