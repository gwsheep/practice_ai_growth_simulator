package com.devgwon.growthsimulator.character.service;

import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import org.springframework.stereotype.Service;

@Service
public class CharacterMessageService {

    public String defaultMessage() {
        return "오늘은 작은 퀘스트 하나만 깨도 충분해요.";
    }

    public String completeMessage(GrowthSubCategory subCategory, boolean levelUp) {
        if (levelUp) {
            return "레벨업! 지금 성장 곡선이 아주 좋아요.";
        }

        String categoryName = subCategory.getCategory().getName();
        String displayName = subCategory.getDisplayName();
        return switch (categoryName) {
            case "SECURITY" -> "보안 퀘스트 완료! 보안 감각이 조금 성장했어요.";
            case "AI" -> "AI 퀘스트 완료! AI 활용력이 올라갔어요.";
            case "TESTING" -> "테스트 퀘스트 완료! 안정적인 코드에 한 걸음 가까워졌어요.";
            case "MENTAL" -> displayName + " 퀘스트 완료! 쉬는 것도 성장입니다.";
            case "SYSTEM_DESIGN" -> "시스템 설계 퀘스트 완료! 설계력이 한 단계 성장했어요.";
            case "ALGORITHM" -> displayName + " 퀘스트 완료! 알고리즘 근육이 조금 붙었어요.";
            case "DATABASE" -> displayName + " 퀘스트 완료! 데이터 감각이 올라갔어요.";
            case "INFRA" -> displayName + " 퀘스트 완료! 운영 경험치가 쌓였어요.";
            case "CAREER" -> displayName + " 퀘스트 완료! 커리어 준비에 한 걸음 가까워졌어요.";
            default -> displayName + " 퀘스트 완료! 개발자 경험치가 쌓였어요.";
        };
    }
}
