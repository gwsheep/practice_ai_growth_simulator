package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.devgwon.growthsimulator.dto.request.AiCoachRequest;
import com.devgwon.growthsimulator.dto.response.AiCoachResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class FakeAiCoachClientTest {
    private final FakeAiCoachClient client = new FakeAiCoachClient();

    @Test
    void generateReturnsDefaultGuideWhenDataIsEmpty() {
        AiCoachResponse response = client.generate(new AiCoachRequest(
                "devgwon",
                1,
                0,
                0,
                "아직 회고가 없습니다.",
                0,
                "아직 없음",
                0,
                0,
                0,
                "아직 없음",
                List.of(),
                List.of()
        ));

        assertThat(response.coachMessage()).contains("작은 Quest");
        assertThat(response.growthSummary()).contains("아직");
        assertThat(response.reviewSummary()).contains("아직");
        assertThat(response.recommendedActions()).hasSize(3);
        assertThat(response.fallback()).isFalse();
    }

    @Test
    void generateReturnsGrowthBasedGuideWhenGrowthLogsExist() {
        AiCoachResponse response = client.generate(new AiCoachRequest(
                "devgwon",
                2,
                20,
                1,
                "JPA 테스트를 정리했다.",
                4,
                "좋음",
                2,
                2,
                70,
                "JPA",
                List.of("트랜잭션 테스트 작성"),
                List.of("JPA +30 EXP", "테스트 +40 EXP")
        ));

        assertThat(response.coachMessage()).contains("JPA");
        assertThat(response.growthSummary()).contains("70 EXP");
        assertThat(response.reviewSummary()).contains("좋음");
        assertThat(response.recommendedActions()).anyMatch(action -> action.contains("JPA"));
    }
}
