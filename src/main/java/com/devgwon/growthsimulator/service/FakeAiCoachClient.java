package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.dto.request.AiCoachRequest;
import com.devgwon.growthsimulator.dto.response.AiCoachResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FakeAiCoachClient implements AiCoachClient {
    @Override
    public AiCoachResponse generate(AiCoachRequest request) {
        boolean empty = request.completedQuestCount() == 0
                && request.recentGrowthLogCount() == 0
                && request.recentReviewCount() == 0;
        if (empty) {
            return new AiCoachResponse(
                    "아직 기록이 많지 않아도 괜찮아요. 오늘 작은 Quest 하나와 짧은 회고 하나부터 시작해볼까요?",
                    "백둥이는 시작한 날도 성장 데이터로 봐요.",
                    "최근 성장 기록이 아직 없습니다.",
                    "최근 회고가 아직 없습니다.",
                    List.of("작은 Quest 1개 만들기", "오늘 회고를 한 줄로 남기기", "성장 항목 하나를 정리하기"),
                    false
            );
        }

        String coachMessage = buildCoachMessage(request);
        String encouragementMessage = request.latestMoodScore() > 0 && request.latestMoodScore() <= 2
                ? "컨디션이 낮은 날에는 쉬운 Quest 하나만 해도 충분해요."
                : "지금 흐름을 유지하되, 다음 행동은 작게 쪼개서 가져가요.";
        return new AiCoachResponse(
                coachMessage,
                encouragementMessage,
                buildGrowthSummary(request),
                buildReviewSummary(request),
                buildRecommendedActions(request),
                false
        );
    }

    private String buildCoachMessage(AiCoachRequest request) {
        if (request.recentGrowthLogCount() > 0) {
            return request.nickname() + "님, 최근 " + request.topGrowthName() + " 쪽 성장이 눈에 띄어요. "
                    + "오늘은 그 흐름을 이어갈 작은 Quest를 추천할게요.";
        }
        if (request.recentReviewCount() > 0) {
            return request.nickname() + "님, 회고를 남긴 것만으로도 다음 행동을 찾을 단서가 생겼어요.";
        }
        return request.nickname() + "님, 완료한 Quest가 있어요. 기록을 조금 더 쌓으면 코칭이 더 선명해질 거예요.";
    }

    private String buildGrowthSummary(AiCoachRequest request) {
        if (request.recentGrowthLogCount() == 0) {
            return "최근 GrowthLog는 아직 없어요. Quest를 완료하면 성장 요약이 채워집니다.";
        }
        return "최근 GrowthLog " + request.recentGrowthLogCount()
                + "개에서 " + request.recentGainedExp()
                + " EXP가 쌓였고, 주요 성장 항목은 " + request.topGrowthName() + "입니다.";
    }

    private String buildReviewSummary(AiCoachRequest request) {
        if (request.recentReviewCount() == 0) {
            return "최근 회고가 아직 없어요. 오늘 배운 것 한 줄만 남겨도 좋아요.";
        }
        return "최근 회고 " + request.recentReviewCount()
                + "개가 있고, 최근 기분은 " + request.latestMoodLabel()
                + "입니다. 메모: " + request.latestReviewSummary();
    }

    private List<String> buildRecommendedActions(AiCoachRequest request) {
        List<String> actions = new ArrayList<>();
        if (request.latestMoodScore() > 0 && request.latestMoodScore() <= 2) {
            actions.add("난이도 EASY Quest 하나만 완료하기");
            actions.add("회고에 오늘 힘들었던 점을 한 줄로 남기기");
        } else if (request.recentGrowthLogCount() > 0) {
            actions.add(request.topGrowthName() + " 관련 Quest 하나 더 이어가기");
            actions.add("최근 GrowthLog를 보고 배운 내용을 회고에 정리하기");
        } else {
            actions.add("완료 가능한 작은 Quest 하나 등록하기");
            actions.add("오늘 배운 내용을 Daily Review에 남기기");
        }
        if (request.recentQuestTitles().isEmpty()) {
            actions.add("Quest 보드에서 오늘 할 일을 하나 만들기");
        } else {
            actions.add("최근 Quest '" + request.recentQuestTitles().get(0) + "'에서 다음 작은 단계 정하기");
        }
        return actions.stream().limit(3).toList();
    }
}
