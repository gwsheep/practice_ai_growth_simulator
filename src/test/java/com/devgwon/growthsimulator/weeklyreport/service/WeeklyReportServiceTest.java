package com.devgwon.growthsimulator.weeklyreport.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthLog;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.quest.domain.Difficulty;
import com.devgwon.growthsimulator.quest.domain.Quest;
import com.devgwon.growthsimulator.quest.domain.QuestStatus;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeeklyReportServiceTest {

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private GrowthLogRepository growthLogRepository;

    @Mock
    private DailyReviewRepository dailyReviewRepository;

    @InjectMocks
    private WeeklyReportService weeklyReportService;

    @Test
    void getReportAggregatesWeeklyQuestGrowthAndReview() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory backend = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthCategory algorithm = new GrowthCategory("ALGORITHM", "알고리즘", "desc", 2);
        GrowthSubCategory api = new GrowthSubCategory(backend, "API", "API 설계", "desc", 1);
        GrowthSubCategory codingTest = new GrowthSubCategory(algorithm, "CODING_TEST", "코딩테스트", "desc", 1);
        Quest quest = new Quest(profile, api, "API 문서 정리", "desc", Difficulty.NORMAL);
        quest.complete();
        GrowthLog apiLog = new GrowthLog(profile, quest, api, 30, "API 설계 성장");
        GrowthLog algorithmLog = new GrowthLog(profile, quest, codingTest, 60, "코딩테스트 성장");
        DailyReview review = new DailyReview(
                profile,
                LocalDate.of(2026, 5, 14),
                "주간 리포트 테스트",
                "집계 확인",
                "화면 확인",
                4,
                api
        );
        LocalDate weekStart = LocalDate.of(2026, 5, 11);
        LocalDate weekEnd = LocalDate.of(2026, 5, 17);

        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(questRepository.findByProfileAndStatusAndCompletedAtBetweenOrderByCompletedAtDesc(
                eq(profile),
                eq(QuestStatus.COMPLETED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(quest));
        when(growthLogRepository.findByProfileAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(profile),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(apiLog, algorithmLog));
        when(dailyReviewRepository.findByProfileAndReviewDateBetweenOrderByReviewDateDesc(
                profile,
                weekStart,
                weekEnd
        )).thenReturn(List.of(review));

        WeeklyReportView report = weeklyReportService.getReport(LocalDate.of(2026, 5, 14));

        assertThat(report.weekStartDate()).isEqualTo(weekStart);
        assertThat(report.weekEndDate()).isEqualTo(weekEnd);
        assertThat(report.completedQuestCount()).isEqualTo(1);
        assertThat(report.totalGainedExp()).isEqualTo(90);
        assertThat(report.mostGrownCategoryName()).isEqualTo("알고리즘");
        assertThat(report.mostGrownSubCategoryName()).isEqualTo("코딩테스트");
        assertThat(report.averageMoodScore()).isEqualTo(4.0);
        assertThat(report.completedQuests()).hasSize(1);
        assertThat(report.categoryGrowthSummaries())
                .extracting("categoryDisplayName", "expGained")
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("알고리즘", 60),
                        org.assertj.core.groups.Tuple.tuple("백엔드", 30)
                );
        assertThat(report.dailyReviewSummaries())
                .extracting("learnedSummary")
                .containsExactly("주간 리포트 테스트");
        assertThat(report.empty()).isFalse();
    }

    @Test
    void getReportReturnsEmptySummaryWhenNoWeeklyDataExists() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        LocalDate date = LocalDate.now();
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(questRepository.findByProfileAndStatusAndCompletedAtBetweenOrderByCompletedAtDesc(
                eq(profile),
                eq(QuestStatus.COMPLETED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());
        when(growthLogRepository.findByProfileAndCreatedAtBetweenOrderByCreatedAtDesc(
                eq(profile),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of());
        when(dailyReviewRepository.findByProfileAndReviewDateBetweenOrderByReviewDateDesc(
                profile,
                weekStart,
                weekEnd
        )).thenReturn(List.of());

        WeeklyReportView report = weeklyReportService.getReport(date);

        assertThat(report.empty()).isTrue();
        assertThat(report.completedQuestCount()).isZero();
        assertThat(report.totalGainedExp()).isZero();
        assertThat(report.mostGrownCategoryName()).isEqualTo("아직 없음");
        assertThat(report.baekdungiMessage()).contains("작은 Quest");
    }
}
