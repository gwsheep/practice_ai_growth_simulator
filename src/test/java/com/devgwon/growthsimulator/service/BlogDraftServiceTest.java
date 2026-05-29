package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.common.BlogDraftSourceType;
import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.dto.request.BlogDraftGenerateRequest;
import com.devgwon.growthsimulator.dto.response.BlogDraftFormView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.ErrorSeverity;
import com.devgwon.growthsimulator.entity.ErrorStatus;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BlogDraftServiceTest {
    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private DailyReviewRepository dailyReviewRepository;

    @Mock
    private ErrorRecordRepository errorRecordRepository;

    @Mock
    private GrowthLogRepository growthLogRepository;

    private BlogDraftService blogDraftService;

    private DeveloperProfile profile;
    private GrowthSubCategory subCategory;

    @BeforeEach
    void setUp() {
        blogDraftService = new BlogDraftService(
                profileService,
                dailyReviewRepository,
                errorRecordRepository,
                growthLogRepository,
                new TemplateBasedBlogDraftGenerator()
        );
        profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "Backend", "desc", 1);
        subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        lenient().when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
    }

    @Test
    void generateDailyReviewDraftUsesTemplateMarkdown() {
        DailyReview review = new DailyReview(
                profile,
                LocalDate.of(2026, 5, 29),
                "트랜잭션 테스트를 정리했다.",
                "fixture 범위가 헷갈렸다.",
                "Controller 테스트를 보강한다.",
                4,
                subCategory
        );
        when(dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of(review));
        emptyErrorRecordsAndGrowthLogs();

        BlogDraftFormView result = blogDraftService.generate(request(BlogDraftSourceType.DAILY_REVIEW));

        assertThat(result.draft().empty()).isFalse();
        assertThat(result.draft().markdown()).contains("## 오늘 배운 것", "트랜잭션 테스트", "## 다음 액션");
    }

    @Test
    void generateErrorRecordDraftUsesTemplateMarkdown() {
        ErrorRecord errorRecord = new ErrorRecord(
                profile,
                "PostgreSQL 연결 거부",
                "Connection refused",
                "컨테이너 접속 중 발생",
                "포트가 열리지 않았다.",
                "포트 매핑을 확인했다.",
                "",
                ErrorStatus.RESOLVED,
                ErrorSeverity.HIGH,
                LocalDateTime.of(2026, 5, 29, 10, 0),
                subCategory,
                null
        );
        when(errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile))
                .thenReturn(List.of(errorRecord));
        when(dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of());
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of());

        BlogDraftFormView result = blogDraftService.generate(request(BlogDraftSourceType.ERROR_RECORD));

        assertThat(result.draft().empty()).isFalse();
        assertThat(result.draft().markdown()).contains("## 문제 상황", "Connection refused", "## 재발 방지 체크리스트");
    }

    @Test
    void generateGrowthLogDraftUsesRecentGrowthLogs() {
        Quest quest = new Quest(profile, subCategory, "Controller 테스트 작성", "desc", Difficulty.NORMAL);
        GrowthLog growthLog = new GrowthLog(profile, quest, subCategory, 30, "API 설계 +30 EXP");
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of(growthLog));
        when(dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of());
        when(errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile)).thenReturn(List.of());

        BlogDraftFormView result = blogDraftService.generate(request(BlogDraftSourceType.GROWTH_LOG));

        assertThat(result.draft().empty()).isFalse();
        assertThat(result.draft().markdown()).contains("## 학습 주제", "Controller 테스트 작성", "+30 EXP");
    }

    @Test
    void generateReturnsEmptyGuideWhenSelectedSourceHasNoData() {
        when(dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of());
        when(errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile)).thenReturn(List.of());
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of());

        BlogDraftFormView result = blogDraftService.generate(request(BlogDraftSourceType.DAILY_REVIEW));

        assertThat(result.draft().empty()).isTrue();
        assertThat(result.draft().guideMessage()).contains("Daily Review");
    }

    @Test
    void generateRejectsMissingSourceTypeSafely() {
        BlogDraftGenerateRequest request = BlogDraftGenerateRequest.createDefault();
        request.setSourceType(null);

        assertThatThrownBy(() -> blogDraftService.generate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("초안 생성 소스");
    }

    private BlogDraftGenerateRequest request(BlogDraftSourceType sourceType) {
        BlogDraftGenerateRequest request = BlogDraftGenerateRequest.createDefault();
        request.setSourceType(sourceType);
        request.setDraftType(switch (sourceType) {
            case DAILY_REVIEW -> BlogDraftType.RETROSPECTIVE;
            case ERROR_RECORD -> BlogDraftType.PROBLEM_SOLVING;
            case GROWTH_LOG -> BlogDraftType.LEARNING_LOG;
        });
        return request;
    }

    private void emptyErrorRecordsAndGrowthLogs() {
        when(errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile)).thenReturn(List.of());
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of());
    }
}
