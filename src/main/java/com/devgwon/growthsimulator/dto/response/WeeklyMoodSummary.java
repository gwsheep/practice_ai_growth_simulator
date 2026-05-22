package com.devgwon.growthsimulator.dto.response;

public record WeeklyMoodSummary(
        int reviewCount,
        double averageMoodScore,
        Integer minMoodScore,
        Integer maxMoodScore,
        String averageMoodLabel
) {

    public static WeeklyMoodSummary empty() {
        return new WeeklyMoodSummary(0, 0.0, null, null, "아직 없음");
    }

    public static String label(double moodScore) {
        if (moodScore <= 0) {
            return "아직 없음";
        }
        return DailyReviewView.moodLabel((int) Math.round(moodScore));
    }
}
