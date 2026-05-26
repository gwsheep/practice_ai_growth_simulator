package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.request.AiCoachRequest;
import com.devgwon.growthsimulator.dto.response.AiCoachResponse;
import com.devgwon.growthsimulator.dto.response.AiCoachView;
import com.devgwon.growthsimulator.dto.response.DailyReviewView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.entity.QuestStatus;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AiCoachService {
    private final DeveloperProfileService profileService;
    private final DailyReviewRepository dailyReviewRepository;
    private final QuestRepository questRepository;
    private final GrowthLogRepository growthLogRepository;
    private final AiCoachClient aiCoachClient;
    private final CharacterMessageService characterMessageService;

    @Transactional(readOnly = true)
    public AiCoachView getCoachForDefaultProfile() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<DailyReview> reviews = dailyReviewRepository.findTop3ByProfileOrderByReviewDateDesc(profile);
        List<Quest> completedQuests = questRepository.findTop5ByProfileAndStatusOrderByCompletedAtDesc(
                profile,
                QuestStatus.COMPLETED
        );
        List<GrowthLog> growthLogs = growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile);
        AiCoachRequest request = buildRequest(profile, reviews, completedQuests, growthLogs);
        boolean empty = reviews.isEmpty() && completedQuests.isEmpty() && growthLogs.isEmpty();
        try {
            return AiCoachView.from(aiCoachClient.generate(request), empty);
        } catch (RuntimeException exception) {
            return AiCoachView.from(fallbackResponse(), empty);
        }
    }

    private AiCoachRequest buildRequest(
            DeveloperProfile profile,
            List<DailyReview> reviews,
            List<Quest> completedQuests,
            List<GrowthLog> growthLogs
    ) {
        DailyReview latestReview = reviews.isEmpty() ? null : reviews.get(0);
        int recentGainedExp = growthLogs.stream()
                .mapToInt(GrowthLog::getExpGained)
                .sum();
        return new AiCoachRequest(
                profile.getNickname(),
                profile.getLevel(),
                profile.getExp(),
                reviews.size(),
                latestReview == null ? "아직 회고가 없습니다." : summarize(latestReview.getLearnedText()),
                latestReview == null ? 0 : latestReview.getMoodScore(),
                latestReview == null ? "아직 없음" : DailyReviewView.moodLabel(latestReview.getMoodScore()),
                completedQuests.size(),
                growthLogs.size(),
                recentGainedExp,
                topGrowthName(growthLogs),
                completedQuests.stream()
                        .map(Quest::getTitle)
                        .toList(),
                growthLogs.stream()
                        .map(log -> log.getSubCategory().getDisplayName() + " +" + log.getExpGained() + " EXP")
                        .toList()
        );
    }

    private String topGrowthName(List<GrowthLog> growthLogs) {
        if (growthLogs.isEmpty()) {
            return "아직 없음";
        }
        Map<String, Integer> expBySubCategory = new LinkedHashMap<>();
        for (GrowthLog log : growthLogs) {
            expBySubCategory.merge(log.getSubCategory().getDisplayName(), log.getExpGained(), Integer::sum);
        }
        return expBySubCategory.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("아직 없음");
    }

    private AiCoachResponse fallbackResponse() {
        return new AiCoachResponse(
                characterMessageService.defaultMessage(),
                "지금은 기본 코칭 메시지로 안내할게요.",
                "성장 요약을 불러오지 못했지만, 기록은 안전하게 유지되고 있어요.",
                "회고 요약을 불러오지 못했어요.",
                List.of("작은 Quest 하나 확인하기", "Daily Review 한 줄 남기기", "Dashboard에서 성장 상태 보기"),
                true
        );
    }

    private String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "배운 내용을 짧게 남겨보세요.";
        }
        String normalized = text.strip();
        return normalized.length() <= 60 ? normalized : normalized.substring(0, 60) + "...";
    }
}
