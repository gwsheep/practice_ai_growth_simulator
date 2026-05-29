package com.devgwon.growthsimulator.service;

import com.devgwon.growthsimulator.common.BlogDraftSourceType;
import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.dto.request.BlogDraftGenerateRequest;
import com.devgwon.growthsimulator.dto.response.BlogDraftFormView;
import com.devgwon.growthsimulator.dto.response.BlogDraftSourceOption;
import com.devgwon.growthsimulator.dto.response.BlogDraftView;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BlogDraftService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final DeveloperProfileService profileService;
    private final DailyReviewRepository dailyReviewRepository;
    private final ErrorRecordRepository errorRecordRepository;
    private final GrowthLogRepository growthLogRepository;
    private final BlogDraftGenerator blogDraftGenerator;

    @Transactional(readOnly = true)
    public BlogDraftFormView getFormView() {
        return buildFormView(
                BlogDraftGenerateRequest.createDefault(),
                BlogDraftView.empty("초안 생성 소스와 유형을 선택하면 Markdown 초안을 만들 수 있어요.")
        );
    }

    @Transactional(readOnly = true)
    public BlogDraftFormView generate(BlogDraftGenerateRequest request) {
        validate(request);
        BlogDraftView draft = switch (request.getSourceType()) {
            case DAILY_REVIEW -> generateDailyReviewDraft(request);
            case ERROR_RECORD -> generateErrorRecordDraft(request);
            case GROWTH_LOG -> generateGrowthLogDraft(request);
        };
        return buildFormView(request, draft);
    }

    private BlogDraftView generateDailyReviewDraft(BlogDraftGenerateRequest request) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<DailyReview> reviews = dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile);
        if (reviews.isEmpty()) {
            return BlogDraftView.empty("Daily Review가 아직 없어요. 회고를 먼저 남기면 회고형 초안을 만들 수 있어요.");
        }
        Long sourceId = request.getSourceId();
        DailyReview review = sourceId == null
                ? reviews.get(0)
                : reviews.stream()
                .filter(candidate -> candidate.getId().equals(sourceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("선택한 Daily Review를 찾을 수 없습니다."));
        return blogDraftGenerator.generateFromDailyReview(review, request.getDraftType());
    }

    private BlogDraftView generateErrorRecordDraft(BlogDraftGenerateRequest request) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<ErrorRecord> errorRecords = errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile);
        if (errorRecords.isEmpty()) {
            return BlogDraftView.empty("Error Museum 기록이 아직 없어요. 에러 수집품을 남기면 문제 해결형 초안을 만들 수 있어요.");
        }
        Long sourceId = request.getSourceId();
        ErrorRecord errorRecord = sourceId == null
                ? errorRecords.get(0)
                : errorRecords.stream()
                .filter(candidate -> candidate.getId().equals(sourceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("선택한 Error Record를 찾을 수 없습니다."));
        return blogDraftGenerator.generateFromErrorRecord(errorRecord, request.getDraftType());
    }

    private BlogDraftView generateGrowthLogDraft(BlogDraftGenerateRequest request) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<GrowthLog> growthLogs = growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile);
        if (growthLogs.isEmpty()) {
            return BlogDraftView.empty("GrowthLog가 아직 없어요. Quest를 완료하면 학습 기록형 초안을 만들 수 있어요.");
        }
        return blogDraftGenerator.generateFromGrowthLogs(growthLogs, request.getDraftType());
    }

    private BlogDraftFormView buildFormView(BlogDraftGenerateRequest request, BlogDraftView draft) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        List<DailyReview> reviews = dailyReviewRepository.findByProfileOrderByReviewDateDesc(profile);
        List<ErrorRecord> errorRecords = errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile);
        List<GrowthLog> growthLogs = growthLogRepository.findTop5ByProfileOrderByCreatedAtDesc(profile);
        boolean empty = reviews.isEmpty() && errorRecords.isEmpty() && growthLogs.isEmpty();
        return new BlogDraftFormView(
                request,
                draft,
                Arrays.asList(BlogDraftSourceType.values()),
                Arrays.asList(BlogDraftType.values()),
                reviews.stream()
                        .map(review -> new BlogDraftSourceOption(
                                review.getId(),
                                review.getReviewDate().format(DATE_FORMATTER) + " 회고"
                        ))
                        .toList(),
                errorRecords.stream()
                        .map(errorRecord -> new BlogDraftSourceOption(
                                errorRecord.getId(),
                                errorRecord.getTitle() + " · " + errorRecord.getErrorName()
                        ))
                        .toList(),
                growthLogs.stream()
                        .map(log -> new BlogDraftSourceOption(
                                log.getId(),
                                formatCreatedAt(log)
                                        + " · " + log.getSubCategory().getDisplayName()
                                        + " +" + log.getExpGained() + " EXP"
                        ))
                        .toList(),
                empty
        );
    }

    private void validate(BlogDraftGenerateRequest request) {
        if (request.getSourceType() == null) {
            throw new IllegalArgumentException("초안 생성 소스를 선택해 주세요.");
        }
        if (request.getDraftType() == null) {
            throw new IllegalArgumentException("초안 유형을 선택해 주세요.");
        }
    }

    private String formatCreatedAt(GrowthLog growthLog) {
        if (growthLog.getCreatedAt() == null) {
            return "기록 시각 확인 필요";
        }
        return growthLog.getCreatedAt().format(DATE_TIME_FORMATTER);
    }
}
