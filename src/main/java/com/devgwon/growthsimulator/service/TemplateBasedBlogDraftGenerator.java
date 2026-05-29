package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.dto.response.BlogDraftView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TemplateBasedBlogDraftGenerator implements BlogDraftGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Override
    public BlogDraftView generateFromDailyReview(DailyReview review, BlogDraftType draftType) {
        String title = review.getReviewDate().format(DATE_FORMATTER) + " 개발 회고";
        String growthLabel = growthLabel(review.getRelatedGrowthSubCategory());
        String markdown = """
                # %s

                ## 오늘 배운 것
                %s

                ## 어려웠던 점
                %s

                ## 내일 시도할 것
                %s

                ## 느낀 점
                TODO: 오늘의 감정과 배운 점을 내 말로 정리하기

                ## 다음 액션
                - TODO: 다음에 바로 실행할 작은 Quest 적기

                ## 연결된 성장 항목
                - %s
                """.formatted(
                title,
                fallback(review.getLearnedText(), "TODO: 오늘 배운 내용을 적기"),
                fallback(review.getDifficultyText(), "TODO: 어려웠던 점을 적기"),
                fallback(review.getTomorrowPlanText(), "TODO: 내일 시도할 일을 적기"),
                growthLabel
        );
        return new BlogDraftView(
                title,
                "Daily Review · " + review.getReviewDate().format(DATE_FORMATTER),
                draftType.getLabel(),
                markdown.strip(),
                "Daily Review를 회고형 Markdown 초안으로 정리했어요. TODO를 내 문장으로 다듬어 주세요.",
                false
        );
    }

    @Override
    public BlogDraftView generateFromErrorRecord(ErrorRecord errorRecord, BlogDraftType draftType) {
        String title = errorRecord.getTitle() + " 해결 기록";
        String markdown = """
                # %s

                ## 문제 상황
                %s

                ## 에러 메시지 또는 증상
                - `%s`

                ## 원인 분석
                %s

                ## 해결 방법
                %s

                ## 배운 점
                TODO: 이 에러를 통해 배운 개념이나 판단 기준 적기

                ## 재발 방지 체크리스트
                - TODO: 같은 문제가 다시 생겼을 때 먼저 확인할 로그/설정 적기
                - TODO: 테스트나 문서로 남길 부분 적기

                ## 연결된 성장 항목
                - %s / %s

                ## 연결 Quest
                - %s
                """.formatted(
                title,
                fallback(errorRecord.getSituation(), "TODO: 문제가 발생한 맥락 적기"),
                fallback(errorRecord.getErrorName(), "증상 미정"),
                fallback(errorRecord.getCause(), "TODO: 원인 분석 적기"),
                fallback(errorRecord.getSolution(), "TODO: 해결 방법 적기"),
                errorRecord.getGrowthSubCategory().getCategory().getDisplayName(),
                errorRecord.getGrowthSubCategory().getDisplayName(),
                errorRecord.getQuest() == null ? "연결된 Quest 없음" : errorRecord.getQuest().getTitle()
        );
        return new BlogDraftView(
                title,
                "Error Museum · " + errorRecord.getTitle(),
                draftType.getLabel(),
                markdown.strip(),
                "Error Museum 기록을 문제 해결형 Markdown 초안으로 정리했어요. 원인과 재발 방지 항목을 보강해 주세요.",
                false
        );
    }

    @Override
    public BlogDraftView generateFromGrowthLogs(List<GrowthLog> growthLogs, BlogDraftType draftType) {
        int totalExp = growthLogs.stream()
                .mapToInt(GrowthLog::getExpGained)
                .sum();
        String title = "최근 학습 기록 요약";
        String growthItems = growthLogs.stream()
                .map(log -> "- " + log.getSubCategory().getCategory().getDisplayName()
                        + " / " + log.getSubCategory().getDisplayName()
                        + ": +" + log.getExpGained() + " EXP")
                .collect(Collectors.joining("\n"));
        String questItems = growthLogs.stream()
                .map(GrowthLog::getQuest)
                .distinct()
                .map(quest -> "- " + quest.getTitle())
                .collect(Collectors.joining("\n"));
        String topGrowth = topGrowth(growthLogs);
        String firstDate = formatCreatedAt(growthLogs.get(growthLogs.size() - 1));
        String lastDate = formatCreatedAt(growthLogs.get(0));
        String markdown = """
                # %s

                ## 기간
                - %s ~ %s

                ## 학습 주제
                - 가장 많이 성장한 항목: %s

                ## 진행한 Quest
                %s

                ## 얻은 경험치와 성장 포인트
                - 총 획득 EXP: %d
                %s

                ## 배운 점
                TODO: 이번 학습에서 가장 많이 이해가 깊어진 부분 적기

                ## 다음 학습 계획
                - TODO: 이어서 진행할 Quest나 복습 계획 적기
                """.formatted(
                title,
                firstDate,
                lastDate,
                topGrowth,
                fallback(questItems, "- TODO: 완료한 Quest 정리하기"),
                totalExp,
                fallback(growthItems, "- TODO: 성장 항목 정리하기")
        );
        return new BlogDraftView(
                title,
                "GrowthLog · 최근 " + growthLogs.size() + "개",
                draftType.getLabel(),
                markdown.strip(),
                "최근 GrowthLog를 학습 기록형 Markdown 초안으로 정리했어요. 배운 점과 다음 계획을 직접 채워 주세요.",
                false
        );
    }

    private String topGrowth(List<GrowthLog> growthLogs) {
        Map<String, Integer> expByGrowth = growthLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getSubCategory().getDisplayName(),
                        Collectors.summingInt(GrowthLog::getExpGained)
                ));
        return expByGrowth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey() + " +" + entry.getValue() + " EXP")
                .orElse("아직 없음");
    }

    private String growthLabel(GrowthSubCategory subCategory) {
        if (subCategory == null) {
            return "성장 항목 미지정";
        }
        return subCategory.getCategory().getDisplayName() + " / " + subCategory.getDisplayName();
    }

    private String formatCreatedAt(GrowthLog growthLog) {
        if (growthLog.getCreatedAt() == null) {
            return "기록 시각 확인 필요";
        }
        return growthLog.getCreatedAt().format(DATE_TIME_FORMATTER);
    }

    private String fallback(String text, String fallback) {
        if (text == null || text.isBlank()) {
            return fallback;
        }
        return text.strip();
    }
}
