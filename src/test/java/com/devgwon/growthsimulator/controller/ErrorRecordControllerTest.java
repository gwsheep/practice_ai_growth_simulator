package com.devgwon.growthsimulator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.ErrorSeverity;
import com.devgwon.growthsimulator.entity.ErrorStatus;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import com.devgwon.growthsimulator.service.DeveloperProfileService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class ErrorRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperProfileService profileService;

    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    @Autowired
    private GrowthCategoryRepository categoryRepository;

    @Autowired
    private GrowthSubCategoryRepository subCategoryRepository;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final List<Long> errorRecordIds = new ArrayList<>();
    private final List<Long> questIds = new ArrayList<>();
    private final List<Long> subCategoryIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        deleteByIds("delete from error_record where id in (:ids)", errorRecordIds);
        deleteByIds("delete from error_record where quest_id in (:ids)", questIds);
        deleteByIds("delete from error_record where growth_sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from quest where id in (:ids)", questIds);
        deleteByIds("delete from quest where sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_sub_category where id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_category where id in (:ids)", categoryIds);
    }

    @Test
    void errorsShowsListModelAndRegisteredRecord() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("목록 확인 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(get("/errors"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/list"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(content().string(containsString(errorRecord.getTitle())));
    }

    @Test
    void newErrorShowsFormModel() throws Exception {
        mockMvc.perform(get("/errors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/form"))
                .andExpect(model().attributeExists("errorForm"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("severities"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("quests"));
    }

    @Test
    void createErrorBindsFormSavesErrorRecordAndRedirectsToDetail() throws Exception {
        GrowthSubCategory subCategory = saveSubCategory();
        Quest quest = saveQuest(subCategory);
        String suffix = uniqueSuffix();
        LocalDateTime occurredAt = LocalDateTime.of(2099, 2, 1, 10, 30);

        mockMvc.perform(post("/errors")
                        .param("title", " PostgreSQL 연결 거부 " + suffix + " ")
                        .param("errorName", " Connection refused " + suffix + " ")
                        .param("situation", " Docker PostgreSQL 접속 중 발생 ")
                        .param("cause", " 컨테이너 포트 미노출 ")
                        .param("solution", " 포트 매핑 확인 ")
                        .param("memo", " 다음에는 docker ps 먼저 확인 ")
                        .param("status", ErrorStatus.OPEN.name())
                        .param("severity", ErrorSeverity.HIGH.name())
                        .param("occurredAt", "2099-02-01T10:30")
                        .param("growthSubCategoryId", subCategory.getId().toString())
                        .param("questId", quest.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/errors/*"))
                .andExpect(flash().attribute("noticeMessage", "새 에러 수집품을 박물관에 올렸어요."));

        ErrorRecord saved = findErrorByTitle("PostgreSQL 연결 거부 " + suffix);
        errorRecordIds.add(saved.getId());
        assertThat(saved.getTitle()).isEqualTo("PostgreSQL 연결 거부 " + suffix);
        assertThat(saved.getErrorName()).isEqualTo("Connection refused " + suffix);
        assertThat(saved.getSituation()).isEqualTo("Docker PostgreSQL 접속 중 발생");
        assertThat(saved.getCause()).isEqualTo("컨테이너 포트 미노출");
        assertThat(saved.getSolution()).isEqualTo("포트 매핑 확인");
        assertThat(saved.getMemo()).isEqualTo("다음에는 docker ps 먼저 확인");
        assertThat(saved.getStatus()).isEqualTo(ErrorStatus.OPEN);
        assertThat(saved.getSeverity()).isEqualTo(ErrorSeverity.HIGH);
        assertThat(saved.getOccurredAt()).isEqualTo(occurredAt);
        assertThat(saved.getGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
        assertThat(saved.getQuest().getId()).isEqualTo(quest.getId());
    }

    @Test
    void createErrorReturnsFormWhenValidationFails() throws Exception {
        mockMvc.perform(post("/errors")
                        .param("title", " ")
                        .param("errorName", "ValidationError")
                        .param("situation", " ")
                        .param("solution", " ")
                        .param("severity", ErrorSeverity.MEDIUM.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/form"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attributeHasErrors("errorForm"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("severities"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("quests"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.errorForm",
                        org.hamcrest.Matchers.hasProperty("globalError",
                                org.hamcrest.Matchers.hasProperty("defaultMessage", containsString("에러 기록 제목")))));

        assertThat(findOptionalErrorByTitle("ValidationError")).isEmpty();
    }

    @Test
    void errorDetailShowsErrorRecordContent() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("상세 확인 에러 " + uniqueSuffix(), ErrorStatus.RESOLVED);

        mockMvc.perform(get("/errors/{id}", errorRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/detail"))
                .andExpect(model().attributeExists("errorRecord"))
                .andExpect(content().string(containsString(errorRecord.getTitle())))
                .andExpect(content().string(containsString(errorRecord.getErrorName())))
                .andExpect(content().string(containsString(errorRecord.getSituation())))
                .andExpect(content().string(containsString(errorRecord.getCause())))
                .andExpect(content().string(containsString(errorRecord.getSolution())))
                .andExpect(content().string(containsString(errorRecord.getStatus().getLabel())))
                .andExpect(content().string(containsString(errorRecord.getSeverity().getLabel())));
    }

    @Test
    void editErrorShowsFormModel() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("수정 폼 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(get("/errors/{id}/edit", errorRecord.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/form"))
                .andExpect(model().attributeExists("errorForm"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attribute("errorRecordId", errorRecord.getId()))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("severities"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("quests"));
    }

    @Test
    void updateErrorBindsFormUpdatesErrorRecordAndRedirectsToDetail() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("수정 전 에러 " + uniqueSuffix(), ErrorStatus.OPEN);
        GrowthSubCategory subCategory = saveSubCategory();
        Quest quest = saveQuest(subCategory);

        mockMvc.perform(post("/errors/{id}/edit", errorRecord.getId())
                        .param("title", " 수정된 에러 ")
                        .param("errorName", " UpdatedException ")
                        .param("situation", " 수정된 상황 ")
                        .param("cause", " 수정된 원인 ")
                        .param("solution", " 수정된 해결 ")
                        .param("memo", " 수정된 메모 ")
                        .param("status", ErrorStatus.RESOLVED.name())
                        .param("severity", ErrorSeverity.CRITICAL.name())
                        .param("occurredAt", "2099-03-02T09:15")
                        .param("growthSubCategoryId", subCategory.getId().toString())
                        .param("questId", quest.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errors/" + errorRecord.getId()))
                .andExpect(flash().attribute("noticeMessage", "에러 기록을 수정했습니다."));

        ErrorRecord updated = errorRecordRepository.findById(errorRecord.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("수정된 에러");
        assertThat(updated.getErrorName()).isEqualTo("UpdatedException");
        assertThat(updated.getSituation()).isEqualTo("수정된 상황");
        assertThat(updated.getCause()).isEqualTo("수정된 원인");
        assertThat(updated.getSolution()).isEqualTo("수정된 해결");
        assertThat(updated.getMemo()).isEqualTo("수정된 메모");
        assertThat(updated.getStatus()).isEqualTo(ErrorStatus.RESOLVED);
        assertThat(updated.getSeverity()).isEqualTo(ErrorSeverity.CRITICAL);
        assertThat(updated.getResolvedAt()).isNotNull();
        assertThat(updated.getGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
        assertThat(updated.getQuest().getId()).isEqualTo(quest.getId());
    }

    @Test
    void updateErrorReturnsFormWhenValidationFails() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("수정 실패 전 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(post("/errors/{id}/edit", errorRecord.getId())
                        .param("title", "수정 실패")
                        .param("errorName", " ")
                        .param("situation", " ")
                        .param("solution", " ")
                        .param("severity", ErrorSeverity.LOW.name())
                        .param("growthSubCategoryId", errorRecord.getGrowthSubCategory().getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/form"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attribute("errorRecordId", errorRecord.getId()))
                .andExpect(model().attributeHasErrors("errorForm"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("severities"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("quests"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.errorForm",
                        org.hamcrest.Matchers.hasProperty("globalError",
                                org.hamcrest.Matchers.hasProperty("defaultMessage", containsString("에러 이름")))));

        ErrorRecord unchanged = errorRecordRepository.findById(errorRecord.getId()).orElseThrow();
        assertThat(unchanged.getTitle()).isEqualTo(errorRecord.getTitle());
        assertThat(unchanged.getErrorName()).isEqualTo(errorRecord.getErrorName());
        assertThat(unchanged.getStatus()).isEqualTo(ErrorStatus.OPEN);
    }

    @Test
    void deleteErrorRemovesErrorRecordAndRedirectsToList() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("삭제 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(post("/errors/{id}/delete", errorRecord.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errors"))
                .andExpect(flash().attribute("noticeMessage", "에러 기록을 삭제했습니다."));

        assertThat(errorRecordRepository.findById(errorRecord.getId())).isEmpty();
        errorRecordIds.remove(errorRecord.getId());
    }

    @Test
    void resolveErrorMarksResolvedAndRedirectsToDetail() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("해결 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(post("/errors/{id}/resolve", errorRecord.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errors/" + errorRecord.getId()))
                .andExpect(flash().attribute("noticeMessage", "에러를 해결됨으로 표시했습니다."));

        ErrorRecord resolved = errorRecordRepository.findById(errorRecord.getId()).orElseThrow();
        assertThat(resolved.getStatus()).isEqualTo(ErrorStatus.RESOLVED);
        assertThat(resolved.getResolvedAt()).isNotNull();
    }

    @Test
    void archiveErrorMarksArchivedAndRedirectsToList() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord("보관 에러 " + uniqueSuffix(), ErrorStatus.OPEN);

        mockMvc.perform(post("/errors/{id}/archive", errorRecord.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/errors"))
                .andExpect(flash().attribute("noticeMessage", "에러 기록을 보관함으로 옮겼습니다."));

        ErrorRecord archived = errorRecordRepository.findById(errorRecord.getId()).orElseThrow();
        assertThat(archived.getStatus()).isEqualTo(ErrorStatus.ARCHIVED);
    }

    @Test
    void errorsCanBeFilteredByStatus() throws Exception {
        ErrorRecord open = saveErrorRecord("필터 OPEN " + uniqueSuffix(), ErrorStatus.OPEN);
        ErrorRecord resolved = saveErrorRecord("필터 RESOLVED " + uniqueSuffix(), ErrorStatus.RESOLVED);
        ErrorRecord archived = saveErrorRecord("필터 ARCHIVED " + uniqueSuffix(), ErrorStatus.ARCHIVED);

        assertFilteredIds(ErrorStatus.OPEN, open.getId(), resolved.getId(), archived.getId());
        assertFilteredIds(ErrorStatus.RESOLVED, resolved.getId(), open.getId(), archived.getId());
        assertFilteredIds(ErrorStatus.ARCHIVED, archived.getId(), open.getId(), resolved.getId());
    }

    @Test
    void errorsCanBeSearchedByKeywordAndEmptyResultStillRenders() throws Exception {
        String suffix = uniqueSuffix();
        ErrorRecord matchedByName = saveErrorRecord(
                "검색 대상 이름 " + suffix,
                "NeedleException " + suffix,
                "일반 상황",
                "일반 원인",
                "일반 해결",
                ErrorStatus.OPEN
        );
        ErrorRecord matchedBySolution = saveErrorRecord(
                "검색 대상 해결 " + suffix,
                "OtherException",
                "일반 상황",
                "일반 원인",
                "needle-keyword-" + suffix + " 해결",
                ErrorStatus.OPEN
        );
        ErrorRecord unrelated = saveErrorRecord("검색 제외 " + suffix, ErrorStatus.OPEN);

        var searchResult = mockMvc.perform(get("/errors").param("keyword", "needle"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/list"))
                .andExpect(model().attribute("keyword", "needle"))
                .andReturn();

        assertThat(errorIds(searchResult)).contains(matchedByName.getId(), matchedBySolution.getId())
                .doesNotContain(unrelated.getId());

        mockMvc.perform(get("/errors").param("keyword", "NO_RESULT_" + suffix))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/list"))
                .andExpect(content().string(containsString("아직 수집된 에러가 없어요")));
    }

    private void assertFilteredIds(ErrorStatus status, Long expectedId, Long unexpectedId1, Long unexpectedId2)
            throws Exception {
        var result = mockMvc.perform(get("/errors").param("status", status.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/list"))
                .andExpect(model().attribute("selectedStatus", status))
                .andReturn();

        assertThat(errorIds(result)).contains(expectedId).doesNotContain(unexpectedId1, unexpectedId2);
    }

    private ErrorRecord saveErrorRecord(String title, ErrorStatus status) {
        return saveErrorRecord(
                title,
                "TestException " + uniqueSuffix(),
                "테스트 발생 상황",
                "테스트 원인",
                "테스트 해결 방법",
                status
        );
    }

    private ErrorRecord saveErrorRecord(
            String title,
            String errorName,
            String situation,
            String cause,
            String solution,
            ErrorStatus status
    ) {
        GrowthSubCategory subCategory = saveSubCategory();
        ErrorRecord errorRecord = errorRecordRepository.saveAndFlush(new ErrorRecord(
                defaultProfile(),
                title,
                errorName,
                situation,
                cause,
                solution,
                "테스트 메모",
                status,
                ErrorSeverity.MEDIUM,
                LocalDateTime.of(2099, 1, 1, 9, 0).plusSeconds(errorRecordIds.size()),
                subCategory,
                null
        ));
        errorRecordIds.add(errorRecord.getId());
        return errorRecord;
    }

    private GrowthSubCategory saveSubCategory() {
        String suffix = uniqueSuffix();
        GrowthCategory category = categoryRepository.saveAndFlush(new GrowthCategory(
                "TEST_ERR_" + suffix,
                "테스트 에러",
                "ErrorRecord Controller 테스트용 성장 영역",
                999
        ));
        categoryIds.add(category.getId());
        GrowthSubCategory subCategory = subCategoryRepository.saveAndFlush(new GrowthSubCategory(
                category,
                "TEST_SUB_" + suffix,
                "테스트 에러 중분류",
                "ErrorRecord Controller 테스트용 성장 항목",
                1
        ));
        subCategoryIds.add(subCategory.getId());
        return subCategory;
    }

    private Quest saveQuest(GrowthSubCategory subCategory) {
        Quest quest = questRepository.saveAndFlush(new Quest(
                defaultProfile(),
                subCategory,
                "Error Museum 연결 Quest " + uniqueSuffix(),
                "ErrorRecord Controller 테스트용 Quest",
                Difficulty.EASY
        ));
        questIds.add(quest.getId());
        return quest;
    }

    private ErrorRecord findErrorByTitle(String title) {
        return findOptionalErrorByTitle(title).orElseThrow();
    }

    private java.util.Optional<ErrorRecord> findOptionalErrorByTitle(String title) {
        return errorRecordRepository.findByProfileOrderByOccurredAtDescCreatedAtDesc(defaultProfile()).stream()
                .filter(errorRecord -> errorRecord.getTitle().equals(title)
                        || errorRecord.getErrorName().equals(title))
                .findFirst();
    }

    private List<Long> errorIds(org.springframework.test.web.servlet.MvcResult result) {
        @SuppressWarnings("unchecked")
        List<Object> errors = (List<Object>) result.getModelAndView().getModel().get("errors");
        return errors.stream()
                .map(error -> ((com.devgwon.growthsimulator.dto.response.ErrorRecordListItemView) error).id())
                .toList();
    }

    private DeveloperProfile defaultProfile() {
        return profileService.getOrCreateDefaultProfile();
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private void deleteByIds(String sql, List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(sql, Map.of("ids", ids));
    }
}
