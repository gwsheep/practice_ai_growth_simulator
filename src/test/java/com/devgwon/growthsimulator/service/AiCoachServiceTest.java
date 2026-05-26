package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.AiCoachRequest;
import com.devgwon.growthsimulator.dto.response.AiCoachView;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiCoachServiceTest {
    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private DailyReviewRepository dailyReviewRepository;

    @Mock
    private QuestRepository questRepository;

    @Mock
    private GrowthLogRepository growthLogRepository;

    @Mock
    private AiCoachClient aiCoachClient;

    @Mock
    private CharacterMessageService characterMessageService;

    @InjectMocks
    private AiCoachService aiCoachService;

    @Test
    void getCoachFallsBackWhenClientFails() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(dailyReviewRepository.findTop3ByProfileOrderByReviewDateDesc(profile)).thenReturn(List.of());
        when(questRepository.findTop5ByProfileAndStatusOrderByCompletedAtDesc(
                profile,
                com.devgwon.growthsimulator.entity.QuestStatus.COMPLETED
        )).thenReturn(List.of());
        when(growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile)).thenReturn(List.of());
        when(aiCoachClient.generate(org.mockito.ArgumentMatchers.any(AiCoachRequest.class)))
                .thenThrow(new RuntimeException("fake failure"));
        when(characterMessageService.defaultMessage()).thenReturn("오늘은 작은 퀘스트 하나만 깨도 충분해요.");

        AiCoachView coach = aiCoachService.getCoachForDefaultProfile();

        assertThat(coach.fallback()).isTrue();
        assertThat(coach.empty()).isTrue();
        assertThat(coach.coachMessage()).isEqualTo("오늘은 작은 퀘스트 하나만 깨도 충분해요.");
        assertThat(coach.recommendedActions()).hasSize(3);
    }
}
