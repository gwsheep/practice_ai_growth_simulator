package com.devgwon.growthsimulator.errorrecord.service;

import com.devgwon.growthsimulator.character.domain.DeveloperProfile;
import com.devgwon.growthsimulator.character.service.DeveloperProfileService;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorRecord;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorSeverity;
import com.devgwon.growthsimulator.errorrecord.domain.ErrorStatus;
import com.devgwon.growthsimulator.errorrecord.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.growth.domain.GrowthSubCategory;
import com.devgwon.growthsimulator.growth.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.quest.domain.Quest;
import com.devgwon.growthsimulator.quest.repository.QuestRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErrorRecordService {

    private final ErrorRecordRepository errorRecordRepository;
    private final DeveloperProfileService profileService;
    private final GrowthSubCategoryRepository subCategoryRepository;
    private final QuestRepository questRepository;

    public ErrorRecordService(
            ErrorRecordRepository errorRecordRepository,
            DeveloperProfileService profileService,
            GrowthSubCategoryRepository subCategoryRepository,
            QuestRepository questRepository
    ) {
        this.errorRecordRepository = errorRecordRepository;
        this.profileService = profileService;
        this.subCategoryRepository = subCategoryRepository;
        this.questRepository = questRepository;
    }

    @Transactional(readOnly = true)
    public List<ErrorRecordListItemView> findList(ErrorStatus status, String keyword) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        String normalizedKeyword = normalizeKeyword(keyword);
        return findRecords(profile, status, normalizedKeyword).stream()
                .map(ErrorRecordListItemView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ErrorRecord get(Long id) {
        return errorRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("에러 기록을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public ErrorRecordView findDetail(Long id) {
        return ErrorRecordView.from(get(id));
    }

    @Transactional
    public ErrorRecord create(ErrorRecordForm form) {
        validate(form);
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        GrowthSubCategory subCategory = findSubCategory(form.getGrowthSubCategoryId());
        Quest quest = findQuest(form.getQuestId());
        return errorRecordRepository.save(new ErrorRecord(
                profile,
                normalizeRequired(form.getTitle(), "에러 기록 제목을 입력해 주세요."),
                normalizeRequired(form.getErrorName(), "에러 이름을 입력해 주세요."),
                normalize(form.getSituation()),
                normalize(form.getCause()),
                normalize(form.getSolution()),
                normalize(form.getMemo()),
                form.getStatus() == null ? ErrorStatus.OPEN : form.getStatus(),
                form.getSeverity() == null ? ErrorSeverity.MEDIUM : form.getSeverity(),
                form.getOccurredAt() == null ? LocalDateTime.now() : form.getOccurredAt(),
                subCategory,
                quest
        ));
    }

    @Transactional
    public void update(Long id, ErrorRecordForm form) {
        validate(form);
        ErrorRecord errorRecord = get(id);
        errorRecord.update(
                normalizeRequired(form.getTitle(), "에러 기록 제목을 입력해 주세요."),
                normalizeRequired(form.getErrorName(), "에러 이름을 입력해 주세요."),
                normalize(form.getSituation()),
                normalize(form.getCause()),
                normalize(form.getSolution()),
                normalize(form.getMemo()),
                form.getStatus() == null ? ErrorStatus.OPEN : form.getStatus(),
                form.getSeverity() == null ? ErrorSeverity.MEDIUM : form.getSeverity(),
                form.getOccurredAt() == null ? LocalDateTime.now() : form.getOccurredAt(),
                findSubCategory(form.getGrowthSubCategoryId()),
                findQuest(form.getQuestId())
        );
    }

    @Transactional
    public void delete(Long id) {
        // ErrorRecord는 현재 독립 기록이므로 물리 삭제한다. 연결 데이터가 생기면 archive 중심으로 전환한다.
        errorRecordRepository.delete(get(id));
    }

    @Transactional
    public void resolve(Long id) {
        get(id).resolve();
    }

    @Transactional
    public void archive(Long id) {
        get(id).archive();
    }

    @Transactional(readOnly = true)
    public List<ErrorRecordDashboardView> findRecentForDashboard(int limit) {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return errorRecordRepository.findTop3ByProfileAndStatusNotOrderByOccurredAtDescCreatedAtDesc(
                        profile,
                        ErrorStatus.ARCHIVED
                ).stream()
                .limit(limit)
                .map(ErrorRecordDashboardView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardErrorRecordSummary getDashboardSummary() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return new DashboardErrorRecordSummary(
                errorRecordRepository.countByProfileAndStatus(profile, ErrorStatus.OPEN),
                errorRecordRepository.countByProfileAndStatus(profile, ErrorStatus.RESOLVED),
                errorRecordRepository.findTop3ByProfileAndStatusNotOrderByOccurredAtDescCreatedAtDesc(
                                profile,
                                ErrorStatus.ARCHIVED
                        ).stream()
                        .map(ErrorRecordDashboardView::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public ErrorRecordFormView createFormView() {
        return new ErrorRecordFormView(
                ErrorRecordForm.createDefault(),
                false,
                null,
                Arrays.asList(ErrorStatus.values()),
                Arrays.asList(ErrorSeverity.values()),
                findGrowthSubCategoryOptions(),
                findQuestOptions()
        );
    }

    @Transactional(readOnly = true)
    public ErrorRecordFormView updateFormView(Long id) {
        ErrorRecord errorRecord = get(id);
        return new ErrorRecordFormView(
                ErrorRecordForm.from(errorRecord),
                true,
                id,
                Arrays.asList(ErrorStatus.values()),
                Arrays.asList(ErrorSeverity.values()),
                findGrowthSubCategoryOptions(),
                findQuestOptions()
        );
    }

    private void validate(ErrorRecordForm form) {
        normalizeRequired(form.getTitle(), "에러 기록 제목을 입력해 주세요.");
        normalizeRequired(form.getErrorName(), "에러 이름을 입력해 주세요.");
        if (form.getGrowthSubCategoryId() == null) {
            throw new IllegalArgumentException("관련 성장 중분류를 선택해 주세요.");
        }
        if (form.getSeverity() == null) {
            throw new IllegalArgumentException("심각도를 선택해 주세요.");
        }
        if (normalize(form.getSituation()).isBlank() && normalize(form.getSolution()).isBlank()) {
            throw new IllegalArgumentException("발생 상황 또는 해결 방법 중 하나는 입력해 주세요.");
        }
    }

    private GrowthSubCategory findSubCategory(Long subCategoryId) {
        if (subCategoryId == null) {
            throw new IllegalArgumentException("관련 성장 중분류를 선택해 주세요.");
        }
        return subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("성장 중분류를 찾을 수 없습니다."));
    }

    private Quest findQuest(Long questId) {
        if (questId == null) {
            return null;
        }
        return questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("연결할 Quest를 찾을 수 없습니다."));
    }

    private List<GrowthSubCategoryOption> findGrowthSubCategoryOptions() {
        return subCategoryRepository.findAllByOrderByCategory_SortOrderAscSortOrderAsc().stream()
                .map(GrowthSubCategoryOption::from)
                .toList();
    }

    private List<QuestOption> findQuestOptions() {
        DeveloperProfile profile = profileService.getOrCreateDefaultProfile();
        return questRepository.findByProfileOrderByCreatedAtDesc(profile).stream()
                .map(QuestOption::from)
                .toList();
    }

    private List<ErrorRecord> findRecords(DeveloperProfile profile, ErrorStatus status, String keyword) {
        if (status == null && keyword == null) {
            return errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(profile);
        }
        if (status != null && keyword == null) {
            return errorRecordRepository.findByProfileAndStatusOrderByOccurredAtDescCreatedAtDesc(profile, status);
        }
        if (status == null) {
            return errorRecordRepository.searchByKeyword(profile, keyword);
        }
        return errorRecordRepository.searchByStatusAndKeyword(profile, status, keyword);
    }

    private String normalizeKeyword(String keyword) {
        String normalized = normalize(keyword);
        return normalized.isBlank() ? null : normalized;
    }

    private String normalizeRequired(String text, String message) {
        String normalized = normalize(text);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
