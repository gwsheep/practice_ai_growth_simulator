package com.devgwon.growthsimulator.weeklyreport.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthLog;
import com.devgwon.growthsimulator.growth.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.quest.domain.Quest;
import com.devgwon.growthsimulator.quest.domain.QuestStatus;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeeklyReportService {

    private final DeveloperProfileService profileService;
    private final QuestRepository questRepository;
    private final GrowthLogRepository growthLogRepository;
    private final DailyReviewRepository dailyReviewRepository;

    public WeeklyReportService(
            DeveloperProfileService profileService,
            QuestRepository questRepository,
            GrowthLogRepository growthLogRepository,
            DailyReviewRepository dailyReviewRepository
    ) {
        this.profileService = profileService;
        this.questRepository = questRepository;
        this.growthLogRepository = growthLogRepository;
        this.dailyReviewRepository = dailyReviewRepository;
    }

    @Transactional(readOnly = true)
    public WeeklyReportView getCurrentWeekReport() {
        return getReport(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public WeeklyReportView getReport(LocalDate dateInWeek) {
        LocalDate weekStart = startOfWeek(dateInWeek == null ? LocalDate.now() : dateInWeek);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDateTime startDateTime = weekStart.atStartOfDay();
        LocalDateTime endDateTime = weekEnd.plusDays(1).atStartOfDay().minusNanos(1);

        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<Quest> quests = questRepository.findByProfileAndStatusAndCompletedAtBetweenOrderByCompletedAtDesc(
                profile,
                QuestStatus.COMPLETED,
                startDateTime,
                endDateTime
        );
        List<GrowthLog> growthLogs = growthLogRepository.findByProfileAndCreatedAtBetweenOrderByCreatedAtDesc(
                profile,
                startDateTime,
                endDateTime
        );
        List<DailyReview> dailyReviews = dailyReviewRepository.findByProfileAndReviewDateBetweenOrderByReviewDateDesc(
                profile,
                weekStart,
                weekEnd
        );

        List<WeeklyCategoryGrowthView> categoryGrowths = buildCategoryGrowths(growthLogs);
        List<WeeklySubCategoryGrowthView> subCategoryGrowths = buildSubCategoryGrowths(growthLogs);
        WeeklyMoodSummary moodSummary = buildMoodSummary(dailyReviews);
        int totalGainedExp = growthLogs.stream()
                .mapToInt(GrowthLog::getExpGained)
                .sum();
        String mostGrownCategoryName = categoryGrowths.stream()
                .filter(WeeklyCategoryGrowthView::top)
                .map(WeeklyCategoryGrowthView::categoryDisplayName)
                .findFirst()
                .orElse("아직 없음");
        String mostGrownSubCategoryName = subCategoryGrowths.stream()
                .filter(WeeklySubCategoryGrowthView::top)
                .map(WeeklySubCategoryGrowthView::subCategoryDisplayName)
                .findFirst()
                .orElse("아직 없음");
        boolean empty = quests.isEmpty() && growthLogs.isEmpty() && dailyReviews.isEmpty();

        WeeklyGrowthSummary growthSummary = new WeeklyGrowthSummary(
                totalGainedExp,
                growthLogs.size(),
                mostGrownCategoryName,
                mostGrownSubCategoryName
        );

        return new WeeklyReportView(
                weekStart,
                weekEnd,
                weekStart.minusWeeks(1),
                weekStart.plusWeeks(1),
                quests.size(),
                totalGainedExp,
                mostGrownCategoryName,
                mostGrownSubCategoryName,
                moodSummary.averageMoodScore(),
                quests.stream()
                        .map(WeeklyQuestSummary::from)
                        .toList(),
                categoryGrowths,
                subCategoryGrowths,
                dailyReviews.stream()
                        .map(WeeklyDailyReviewSummary::from)
                        .toList(),
                moodSummary,
                growthSummary,
                buildBaekdungiMessage(quests, growthLogs, dailyReviews, mostGrownCategoryName),
                empty
        );
    }

    @Transactional(readOnly = true)
    public DashboardWeeklyReportSummary getDashboardSummary() {
        WeeklyReportView report = getCurrentWeekReport();
        return new DashboardWeeklyReportSummary(
                report.weekStartDate(),
                report.weekEndDate(),
                report.completedQuestCount(),
                report.totalGainedExp(),
                !report.empty()
        );
    }

    private LocalDate startOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private List<WeeklyCategoryGrowthView> buildCategoryGrowths(List<GrowthLog> logs) {
        Map<String, GrowthAccumulator> grouped = new LinkedHashMap<>();
        for (GrowthLog log : logs) {
            String categoryName = log.getSubCategory().getCategory().getDisplayName();
            grouped.computeIfAbsent(categoryName, key -> new GrowthAccumulator())
                    .add(log.getExpGained());
        }
        int maxExp = grouped.values().stream()
                .mapToInt(GrowthAccumulator::exp)
                .max()
                .orElse(0);
        return grouped.entrySet().stream()
                .map(entry -> new WeeklyCategoryGrowthView(
                        entry.getKey(),
                        entry.getValue().exp(),
                        entry.getValue().count(),
                        maxExp > 0 && entry.getValue().exp() == maxExp
                ))
                .sorted(Comparator.comparingInt(WeeklyCategoryGrowthView::expGained).reversed()
                        .thenComparing(WeeklyCategoryGrowthView::categoryDisplayName))
                .toList();
    }

    private List<WeeklySubCategoryGrowthView> buildSubCategoryGrowths(List<GrowthLog> logs) {
        Map<SubCategoryKey, GrowthAccumulator> grouped = new LinkedHashMap<>();
        for (GrowthLog log : logs) {
            SubCategoryKey key = new SubCategoryKey(
                    log.getSubCategory().getCategory().getDisplayName(),
                    log.getSubCategory().getDisplayName()
            );
            grouped.computeIfAbsent(key, ignored -> new GrowthAccumulator())
                    .add(log.getExpGained());
        }
        int maxExp = grouped.values().stream()
                .mapToInt(GrowthAccumulator::exp)
                .max()
                .orElse(0);
        return grouped.entrySet().stream()
                .map(entry -> new WeeklySubCategoryGrowthView(
                        entry.getKey().categoryDisplayName(),
                        entry.getKey().subCategoryDisplayName(),
                        entry.getValue().exp(),
                        entry.getValue().count(),
                        maxExp > 0 && entry.getValue().exp() == maxExp
                ))
                .sorted(Comparator.comparingInt(WeeklySubCategoryGrowthView::expGained).reversed()
                        .thenComparing(WeeklySubCategoryGrowthView::subCategoryDisplayName))
                .toList();
    }

    private WeeklyMoodSummary buildMoodSummary(List<DailyReview> reviews) {
        if (reviews.isEmpty()) {
            return WeeklyMoodSummary.empty();
        }
        double averageMoodScore = reviews.stream()
                .mapToInt(DailyReview::getMoodScore)
                .average()
                .orElse(0.0);
        int minMoodScore = reviews.stream()
                .mapToInt(DailyReview::getMoodScore)
                .min()
                .orElse(0);
        int maxMoodScore = reviews.stream()
                .mapToInt(DailyReview::getMoodScore)
                .max()
                .orElse(0);
        return new WeeklyMoodSummary(
                reviews.size(),
                Math.round(averageMoodScore * 10) / 10.0,
                minMoodScore,
                maxMoodScore,
                WeeklyMoodSummary.label(averageMoodScore)
        );
    }

    private String buildBaekdungiMessage(
            List<Quest> quests,
            List<GrowthLog> growthLogs,
            List<DailyReview> dailyReviews,
            String mostGrownCategoryName
    ) {
        if (!growthLogs.isEmpty()) {
            return "이번 주도 많이 성장했어요! 가장 많이 성장한 영역은 " + mostGrownCategoryName + "이에요.";
        }
        if (!dailyReviews.isEmpty()) {
            return "이번 주 회고가 쌓이고 있어요. 기록은 다음 성장의 지도예요.";
        }
        if (!quests.isEmpty()) {
            return "완료한 Quest가 있어요. 이제 GrowthLog가 더 쌓이면 리포트가 풍성해져요.";
        }
        return "아직 기록이 적지만 괜찮아요. 오늘 작은 Quest 하나부터 시작해볼까요?";
    }

    private static class GrowthAccumulator {

        private int exp;
        private int count;

        void add(int expGained) {
            this.exp += expGained;
            this.count++;
        }

        int exp() {
            return exp;
        }

        int count() {
            return count;
        }
    }

    private record SubCategoryKey(
            String categoryDisplayName,
            String subCategoryDisplayName
    ) {
    }
}
