package com.devgwon.growthsimulator.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.growth.domain.GrowthCategory;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.review.domain.DailyReview;
import com.devgwon.growthsimulator.review.repository.DailyReviewRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DailyReviewServiceTest {

    @Mock
    private DailyReviewRepository dailyReviewRepository;

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @InjectMocks
    private DailyReviewService dailyReviewService;

    @Test
    void createSavesReviewForDefaultProfile() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        DailyReviewCreateRequest request = new DailyReviewCreateRequest();
        request.setReviewDate(LocalDate.of(2026, 5, 14));
        request.setLearnedText(" JPA 테스트 ");
        request.setDifficultyText("");
        request.setTomorrowPlanText(" Dashboard 연결 ");
        request.setMoodScore(4);
        request.setRelatedGrowthSubCategoryId(1L);

        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(dailyReviewRepository.existsByProfileAndReviewDate(profile, request.getReviewDate())).thenReturn(false);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));

        dailyReviewService.create(request);

        ArgumentCaptor<DailyReview> captor = ArgumentCaptor.forClass(DailyReview.class);
        verify(dailyReviewRepository).save(captor.capture());
        DailyReview saved = captor.getValue();
        assertThat(saved.getProfile()).isSameAs(profile);
        assertThat(saved.getReviewDate()).isEqualTo("2026-05-14");
        assertThat(saved.getLearnedText()).isEqualTo("JPA 테스트");
        assertThat(saved.getTomorrowPlanText()).isEqualTo("Dashboard 연결");
        assertThat(saved.getMoodScore()).isEqualTo(4);
        assertThat(saved.getRelatedGrowthSubCategory()).isSameAs(subCategory);
    }

    @Test
    void createRejectsDuplicateReviewDate() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        DailyReviewCreateRequest request = new DailyReviewCreateRequest();
        request.setReviewDate(LocalDate.of(2026, 5, 14));
        request.setLearnedText("중복 테스트");
        request.setMoodScore(3);

        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(dailyReviewRepository.existsByProfileAndReviewDate(profile, request.getReviewDate())).thenReturn(true);

        assertThatThrownBy(() -> dailyReviewService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 작성");

        verify(dailyReviewRepository, never()).save(any());
    }

    @Test
    void createRejectsInvalidMoodScore() {
        DailyReviewCreateRequest request = new DailyReviewCreateRequest();
        request.setReviewDate(LocalDate.of(2026, 5, 14));
        request.setLearnedText("기분 점수 테스트");
        request.setMoodScore(6);

        assertThatThrownBy(() -> dailyReviewService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("기분 점수");

        verify(dailyReviewRepository, never()).save(any());
    }
}
