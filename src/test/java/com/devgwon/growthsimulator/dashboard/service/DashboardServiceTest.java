package com.devgwon.growthsimulator.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.errorrecord.service.DashboardErrorRecordSummary;
import com.devgwon.growthsimulator.errorrecord.service.ErrorRecordService;
import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.monster.service.DashboardMonsterSummary;
import com.devgwon.growthsimulator.monster.service.MonsterService;
import com.devgwon.growthsimulator.quest.domain.Difficulty;
import com.devgwon.growthsimulator.quest.domain.Quest;
import com.devgwon.growthsimulator.quest.domain.QuestStatus;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.schedule.domain.Schedule;
import com.devgwon.growthsimulator.schedule.domain.ScheduleStatus;
import com.devgwon.growthsimulator.schedule.domain.ScheduleType;
import com.devgwon.growthsimulator.schedule.repository.ScheduleRepository;
import com.devgwon.growthsimulator.weeklyreport.service.DashboardWeeklyReportSummary;
import com.devgwon.growthsimulator.weeklyreport.service.WeeklyReportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private GrowthCategoryRepository categoryRepository;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @Mock
    private GrowthLogRepository growthLogRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DailyReviewRepository dailyReviewRepository;

    @Mock
    private WeeklyReportService weeklyReportService;

    @Mock
    private MonsterService monsterService;

    @Mock
    private ErrorRecordService errorRecordService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardAssemblesGrowthQuestAndScheduleSummary() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        Quest quest = new Quest(profile, subCategory, "API 정리", "desc", Difficulty.EASY);
        ScheduleType scheduleType = new ScheduleType("STUDY", "공부", "desc", "#4d78b8", 1);
        Schedule schedule = new Schedule(
                profile,
                subCategory,
                "오늘 공부",
                "desc",
                LocalDate.now().atTime(19, 0),
                null,
                scheduleType
        );
        DailyReview dailyReview = new DailyReview(
                profile,
                LocalDate.now(),
                "DashboardService 테스트를 작성했다.",
                "",
                "회고 화면 확인",
                4,
                subCategory
        );
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime weekEnd = today.plusDays(7).atStartOfDay();
        List<ScheduleStatus> activeStatuses = List.of(ScheduleStatus.PLANNED, ScheduleStatus.IN_PROGRESS);

        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of());
        when(categoryRepository.findAllByOrderBySortOrderAsc()).thenReturn(List.of(category));
        when(subCategoryRepository.findByCategoryOrderBySortOrderAsc(category)).thenReturn(List.of(subCategory));
        when(questRepository.findByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of(quest));
        when(questRepository.findTop5ByProfileAndStatusOrderByCompletedAtDesc(profile, QuestStatus.COMPLETED))
                .thenReturn(List.of());
        when(scheduleRepository.countByProfileAndStatusInAndStartDateTimeBetween(
                profile,
                activeStatuses,
                todayStart,
                tomorrowStart
        )).thenReturn(1L);
        when(scheduleRepository.countByProfileAndStatusInAndStartDateTimeBetween(
                profile,
                activeStatuses,
                todayStart,
                weekEnd
        )).thenReturn(2L);
        when(scheduleRepository.findTop3ByProfileAndStatusInAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                profile,
                activeStatuses,
                todayStart,
                tomorrowStart
        )).thenReturn(List.of(schedule));
        when(dailyReviewRepository.findByProfileAndReviewDate(profile, today)).thenReturn(java.util.Optional.of(dailyReview));
        when(dailyReviewRepository.findTop3ByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of(dailyReview));
        when(weeklyReportService.getDashboardSummary()).thenReturn(new DashboardWeeklyReportSummary(
                today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)),
                today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)),
                1,
                10,
                true
        ));
        when(monsterService.getDashboardSummary()).thenReturn(new DashboardMonsterSummary(0, null, null, List.of()));
        when(errorRecordService.getDashboardSummary()).thenReturn(new DashboardErrorRecordSummary(0, 0, List.of()));

        DashboardView dashboard = dashboardService.getDashboard();

        assertThat(dashboard.profile().nickname()).isEqualTo("devgwon");
        assertThat(dashboard.growthCategories()).hasSize(1);
        assertThat(dashboard.quests()).hasSize(1);
        assertThat(dashboard.todayScheduleCount()).isEqualTo(1);
        assertThat(dashboard.weekScheduleCount()).isEqualTo(2);
        assertThat(dashboard.todaySchedules())
                .extracting("title")
                .containsExactly("오늘 공부");
        assertThat(dashboard.todayReviewWritten()).isTrue();
        assertThat(dashboard.recentDailyReviews())
                .extracting("learnedSummary")
                .containsExactly("DashboardService 테스트를 작성했다.");
        assertThat(dashboard.weeklyReportSummary().completedQuestCount()).isEqualTo(1);
        assertThat(dashboard.monsterSummary().activeMonsterCount()).isZero();
        assertThat(dashboard.errorRecordSummary().recentErrors()).isEmpty();
    }
}
