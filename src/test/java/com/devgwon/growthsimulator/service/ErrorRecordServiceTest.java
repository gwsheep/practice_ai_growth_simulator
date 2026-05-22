package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.ErrorRecordForm;
import com.devgwon.growthsimulator.dto.response.ErrorRecordListItemView;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.ErrorSeverity;
import com.devgwon.growthsimulator.entity.ErrorStatus;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorRecordServiceTest {
    @Mock
    private ErrorRecordRepository errorRecordRepository;

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @Mock
    private QuestRepository questRepository;

    @InjectMocks
    private ErrorRecordService errorRecordService;

    @Test
    void createSavesErrorRecordWithRequiredGrowthSubCategoryAndOptionalQuest() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("INFRA", "인프라", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "DOCKER", "Docker", "desc", 1);
        Quest quest = new Quest(profile, subCategory, "Docker 연결 확인", "desc", Difficulty.EASY);
        LocalDateTime occurredAt = LocalDateTime.of(2026, 5, 21, 10, 30);
        ErrorRecordForm form = new ErrorRecordForm();
        form.setTitle(" PostgreSQL 연결 거부 ");
        form.setErrorName(" Connection refused ");
        form.setSituation("Docker PostgreSQL 접속 중 발생");
        form.setSolution("포트 매핑 확인");
        form.setSeverity(ErrorSeverity.HIGH);
        form.setStatus(ErrorStatus.OPEN);
        form.setOccurredAt(occurredAt);
        form.setGrowthSubCategoryId(1L);
        form.setQuestId(2L);
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory));
        when(questRepository.findById(2L)).thenReturn(Optional.of(quest));
        errorRecordService.create(form);
        ArgumentCaptor<ErrorRecord> captor = ArgumentCaptor.forClass(ErrorRecord.class);
        verify(errorRecordRepository).save(captor.capture());
        ErrorRecord saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("PostgreSQL 연결 거부");
        assertThat(saved.getErrorName()).isEqualTo("Connection refused");
        assertThat(saved.getStatus()).isEqualTo(ErrorStatus.OPEN);
        assertThat(saved.getSeverity()).isEqualTo(ErrorSeverity.HIGH);
        assertThat(saved.getOccurredAt()).isEqualTo(occurredAt);
        assertThat(saved.getGrowthSubCategory()).isSameAs(subCategory);
        assertThat(saved.getQuest()).isSameAs(quest);
    }

    @Test
    void createRejectsMissingGrowthSubCategory() {
        ErrorRecordForm form = new ErrorRecordForm();
        form.setTitle("에러");
        form.setErrorName("Error");
        form.setSituation("상황");
        form.setSeverity(ErrorSeverity.MEDIUM);
        assertThatThrownBy(() -> errorRecordService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("성장 중분류");
        verify(errorRecordRepository, never()).save(any());
    }

    @Test
    void createRequiresSituationOrSolution() {
        ErrorRecordForm form = new ErrorRecordForm();
        form.setTitle("에러");
        form.setErrorName("Error");
        form.setSeverity(ErrorSeverity.MEDIUM);
        form.setGrowthSubCategoryId(1L);
        assertThatThrownBy(() -> errorRecordService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("발생 상황 또는 해결 방법");
    }

    @Test
    void resolveMarksResolvedAndSetsResolvedAt() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        ErrorRecord errorRecord = new ErrorRecord(
                profile,
                "NPE",
                "NullPointerException",
                "상황",
                "",
                "",
                "",
                ErrorStatus.OPEN,
                ErrorSeverity.MEDIUM,
                LocalDateTime.now(),
                subCategory,
                null
        );
        when(errorRecordRepository.findById(1L)).thenReturn(Optional.of(errorRecord));
        errorRecordService.resolve(1L);
        assertThat(errorRecord.getStatus()).isEqualTo(ErrorStatus.RESOLVED);
        assertThat(errorRecord.getResolvedAt()).isNotNull();
    }

    @Test
    void archiveMarksArchivedWithoutRequiringResolvedAt() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        ErrorRecord errorRecord = new ErrorRecord(
                profile,
                "NPE",
                "NullPointerException",
                "상황",
                "",
                "",
                "",
                ErrorStatus.OPEN,
                ErrorSeverity.LOW,
                LocalDateTime.now(),
                subCategory,
                null
        );
        when(errorRecordRepository.findById(1L)).thenReturn(Optional.of(errorRecord));
        errorRecordService.archive(1L);
        assertThat(errorRecord.getStatus()).isEqualTo(ErrorStatus.ARCHIVED);
        assertThat(errorRecord.getResolvedAt()).isNull();
    }

    @Test
    void findListNormalizesBlankKeywordToNull() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(errorRecordRepository.findByProfileAndStatusOrderByOccurredAtDescCreatedAtDesc(profile, ErrorStatus.OPEN))
                .thenReturn(List.of());
        List<ErrorRecordListItemView> result = errorRecordService.findList(ErrorStatus.OPEN, "   ");
        assertThat(result).isEmpty();
        verify(errorRecordRepository).findByProfileAndStatusOrderByOccurredAtDescCreatedAtDesc(profile, ErrorStatus.OPEN);
    }
}
