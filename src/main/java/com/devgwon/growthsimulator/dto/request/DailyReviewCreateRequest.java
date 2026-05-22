package com.devgwon.growthsimulator.dto.request;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyReviewCreateRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate = LocalDate.now();

    private String learnedText;
    private String difficultyText;
    private String tomorrowPlanText;
    private int moodScore = 3;
    private Long relatedGrowthSubCategoryId;

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
