package com.devgwon.growthsimulator.review.service;

import com.devgwon.growthsimulator.review.domain.DailyReview;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyReviewUpdateRequest {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;

    private String learnedText;
    private String difficultyText;
    private String tomorrowPlanText;
    private int moodScore = 3;
    private Long relatedGrowthSubCategoryId;

    public static DailyReviewUpdateRequest from(DailyReview review) {
        DailyReviewUpdateRequest request = new DailyReviewUpdateRequest();
        request.setReviewDate(review.getReviewDate());
        request.setLearnedText(review.getLearnedText());
        request.setDifficultyText(review.getDifficultyText());
        request.setTomorrowPlanText(review.getTomorrowPlanText());
        request.setMoodScore(review.getMoodScore());
        if (review.getRelatedGrowthSubCategory() != null) {
            request.setRelatedGrowthSubCategoryId(review.getRelatedGrowthSubCategory().getId());
        }
        return request;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getLearnedText() {
        return learnedText;
    }

    public void setLearnedText(String learnedText) {
        this.learnedText = learnedText;
    }

    public String getDifficultyText() {
        return difficultyText;
    }

    public void setDifficultyText(String difficultyText) {
        this.difficultyText = difficultyText;
    }

    public String getTomorrowPlanText() {
        return tomorrowPlanText;
    }

    public void setTomorrowPlanText(String tomorrowPlanText) {
        this.tomorrowPlanText = tomorrowPlanText;
    }

    public int getMoodScore() {
        return moodScore;
    }

    public void setMoodScore(int moodScore) {
        this.moodScore = moodScore;
    }

    public Long getRelatedGrowthSubCategoryId() {
        return relatedGrowthSubCategoryId;
    }

    public void setRelatedGrowthSubCategoryId(Long relatedGrowthSubCategoryId) {
        this.relatedGrowthSubCategoryId = relatedGrowthSubCategoryId;
    }
}
