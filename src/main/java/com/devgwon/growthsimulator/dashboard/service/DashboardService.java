package com.devgwon.growthsimulator.dashboard.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.monster.service.MonsterService;
import com.devgwon.growthsimulator.quest.domain.QuestStatus;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.review.service.DailyReviewSummaryView;
import com.devgwon.growthsimulator.schedule.domain.ScheduleStatus;
import com.devgwon.growthsimulator.schedule.repository.ScheduleRepository;
import com.devgwon.growthsimulator.schedule.service.DashboardScheduleSummary;
import com.devgwon.growthsimulator.weeklyreport.service.WeeklyReportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final DeveloperProfileService profileService;
    private final GrowthCategoryRepository categoryRepository;
    private final GrowthSubCategoryRepository subCategoryRepository;
    private final GrowthLogRepository growthLogRepository;
    private final QuestRepository questRepository;
    private final ScheduleRepository scheduleRepository;
    private final DailyReviewRepository dailyReviewRepository;
    private final WeeklyReportService weeklyReportService;
    private final MonsterService monsterService;

    public DashboardService(
            DeveloperProfileService profileService,
            GrowthCategoryRepository categoryRepository,
            GrowthSubCategoryRepository subCategoryRepository,
            GrowthLogRepository growthLogRepository,
            QuestRepository questRepository,
            ScheduleRepository scheduleRepository,
            DailyReviewRepository dailyReviewRepository,
            WeeklyReportService weeklyReportService,
            MonsterService monsterService
    ) {
        this.profileService = profileService;
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.growthLogRepository = growthLogRepository;
        this.questRepository = questRepository;
        this.scheduleRepository = scheduleRepository;
        this.dailyReviewRepository = dailyReviewRepository;
        this.weeklyReportService = weeklyReportService;
        this.monsterService = monsterService;
    }

    @Transactional
    public DashboardView getDashboard() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<GrowthLogSummaryView> recentGrowthLogs = growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)
                .stream()
                .map(GrowthLogSummaryView::from)
                .toList();
        List<GrowthCategorySectionView> growthCategories = findGrowthCategories();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime weekEnd = today.plusDays(7).atStartOfDay();
        List<ScheduleStatus> plannedStatuses = List.of(ScheduleStatus.PLANNED, ScheduleStatus.IN_PROGRESS);
        DailyReview todayReview = dailyReviewRepository.findByProfileAndReviewDate(profile, today)
                .orElse(null);

        return new DashboardView(
                ProfileProgressView.from(profile),
                GrowthSummaryView.from(growthCategories, recentGrowthLogs),
                growthCategories,
                recentGrowthLogs,
                questRepository.findByProfileOrderByCreatedAtDesc(profile).stream()
                        .map(QuestSummaryView::from)
                        .toList(),
                questRepository.findTop5ByProfileAndStatusOrderByCompletedAtDesc(profile, QuestStatus.COMPLETED).stream()
                        .map(QuestSummaryView::from)
                        .toList(),
                scheduleRepository.countByProfileAndStatusInAndStartDateTimeBetween(
                        profile,
                        plannedStatuses,
                        todayStart,
                        tomorrowStart
                ),
                scheduleRepository.countByProfileAndStatusInAndStartDateTimeBetween(
                        profile,
                        plannedStatuses,
                        todayStart,
                        weekEnd
                ),
                scheduleRepository.findTop3ByProfileAndStatusInAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                                profile,
                                plannedStatuses,
                                todayStart,
                                tomorrowStart
                        ).stream()
                        .map(DashboardScheduleSummary::from)
                        .toList(),
                todayReview != null,
                todayReview == null ? null : todayReview.getId(),
                dailyReviewRepository.findTop3ByProfileOrderByReviewDateDesc(profile).stream()
                        .map(DailyReviewSummaryView::from)
                        .toList(),
                weeklyReportService.getDashboardSummary(),
                monsterService.getDashboardSummary()
        );
    }

    private List<GrowthCategorySectionView> findGrowthCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(category -> GrowthCategorySectionView.of(
                        category,
                        subCategoryRepository.findByCategoryOrderBySortOrderAsc(category).stream()
                                .map(GrowthSubCategoryCardView::from)
                                .toList()
                ))
                .toList();
    }
}
