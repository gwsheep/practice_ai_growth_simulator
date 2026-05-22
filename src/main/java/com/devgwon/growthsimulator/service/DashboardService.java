package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.response.DailyReviewSummaryView;
import com.devgwon.growthsimulator.dto.response.DashboardScheduleSummary;
import com.devgwon.growthsimulator.dto.response.DashboardView;
import com.devgwon.growthsimulator.dto.response.GrowthCategorySectionView;
import com.devgwon.growthsimulator.dto.response.GrowthLogSummaryView;
import com.devgwon.growthsimulator.dto.response.GrowthSubCategoryCardView;
import com.devgwon.growthsimulator.dto.response.GrowthSummaryView;
import com.devgwon.growthsimulator.dto.response.ProfileProgressView;
import com.devgwon.growthsimulator.dto.response.QuestSummaryView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.QuestStatus;
import com.devgwon.growthsimulator.entity.ScheduleStatus;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import com.devgwon.growthsimulator.repository.ScheduleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
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
    private final ErrorRecordService errorRecordService;

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
                        .limit(3)
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
                monsterService.getDashboardSummary(),
                errorRecordService.getDashboardSummary()
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
