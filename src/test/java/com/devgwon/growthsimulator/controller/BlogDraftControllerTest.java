package com.devgwon.growthsimulator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.devgwon.growthsimulator.common.BlogDraftSourceType;
import com.devgwon.growthsimulator.common.BlogDraftType;
import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.entity.ErrorRecord;
import com.devgwon.growthsimulator.entity.ErrorSeverity;
import com.devgwon.growthsimulator.entity.ErrorStatus;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.ErrorRecordRepository;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import com.devgwon.growthsimulator.service.DeveloperProfileService;
import java.time.LocalDate;
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
class BlogDraftControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperProfileService profileService;

    @Autowired
    private DailyReviewRepository dailyReviewRepository;

    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    @Autowired
    private GrowthLogRepository growthLogRepository;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private GrowthCategoryRepository categoryRepository;

    @Autowired
    private GrowthSubCategoryRepository subCategoryRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final List<Long> reviewIds = new ArrayList<>();
    private final List<Long> errorRecordIds = new ArrayList<>();
    private final List<Long> growthLogIds = new ArrayList<>();
    private final List<Long> questIds = new ArrayList<>();
    private final List<Long> subCategoryIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        deleteByIds("delete from growth_log where id in (:ids)", growthLogIds);
        deleteByIds("delete from error_record where id in (:ids)", errorRecordIds);
        deleteByIds("delete from daily_review where id in (:ids)", reviewIds);
        deleteByIds("delete from growth_log where quest_id in (:ids)", questIds);
        deleteByIds("delete from error_record where quest_id in (:ids)", questIds);
        deleteByIds("delete from quest where id in (:ids)", questIds);
        deleteByIds("delete from daily_review where related_sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from error_record where growth_sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from quest where sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_sub_category where id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_category where id in (:ids)", categoryIds);
    }

    @Test
    void blogDraftsShowsGeneratorScreen() throws Exception {
        mockMvc.perform(get("/blog-drafts"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(model().attributeExists("draftRequest"))
                .andExpect(model().attributeExists("draft"))
                .andExpect(model().attributeExists("sourceTypes"))
                .andExpect(model().attributeExists("draftTypes"))
                .andExpect(content().string(containsString("Blog Draft Generator")));
    }

    @Test
    void generateDailyReviewDraftShowsMarkdown() throws Exception {
        DailyReview review = saveReview();

        mockMvc.perform(post("/blog-drafts/generate")
                        .param("sourceType", BlogDraftSourceType.DAILY_REVIEW.name())
                        .param("draftType", BlogDraftType.RETROSPECTIVE.name())
                        .param("sourceId", review.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(content().string(containsString("## 오늘 배운 것")))
                .andExpect(content().string(containsString("MockMvc 회고 초안")));
    }

    @Test
    void generateErrorRecordDraftShowsMarkdown() throws Exception {
        ErrorRecord errorRecord = saveErrorRecord();

        mockMvc.perform(post("/blog-drafts/generate")
                        .param("sourceType", BlogDraftSourceType.ERROR_RECORD.name())
                        .param("draftType", BlogDraftType.PROBLEM_SOLVING.name())
                        .param("sourceId", errorRecord.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(content().string(containsString("## 문제 상황")))
                .andExpect(content().string(containsString("BlogDraftException")));
    }

    @Test
    void generateGrowthLogDraftShowsMarkdown() throws Exception {
        saveGrowthLog();

        mockMvc.perform(post("/blog-drafts/generate")
                        .param("sourceType", BlogDraftSourceType.GROWTH_LOG.name())
                        .param("draftType", BlogDraftType.LEARNING_LOG.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(content().string(containsString("## 학습 주제")))
                .andExpect(content().string(containsString("Blog Draft Quest")));
    }

    @Test
    void generateReturnsGuideWhenSelectedSourceHasNoData() throws Exception {
        mockMvc.perform(post("/blog-drafts/generate")
                        .param("sourceType", BlogDraftSourceType.DAILY_REVIEW.name())
                        .param("draftType", BlogDraftType.RETROSPECTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(content().string(containsString("Daily Review")));
    }

    @Test
    void generateReturnsFormWhenSourceTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/blog-drafts/generate")
                        .param("sourceType", "WRONG_SOURCE")
                        .param("draftType", BlogDraftType.RETROSPECTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog-drafts/index"))
                .andExpect(model().attributeHasErrors("draftRequest"));
    }

    private DailyReview saveReview() {
        GrowthSubCategory subCategory = saveSubCategory();
        DailyReview review = dailyReviewRepository.saveAndFlush(new DailyReview(
                defaultProfile(),
                nextReviewDate(),
                "MockMvc 회고 초안 테스트를 정리했다.",
                "소스 선택 흐름이 헷갈렸다.",
                "BlogDraftService 테스트를 보강한다.",
                4,
                subCategory
        ));
        reviewIds.add(review.getId());
        return review;
    }

    private ErrorRecord saveErrorRecord() {
        GrowthSubCategory subCategory = saveSubCategory();
        ErrorRecord errorRecord = errorRecordRepository.saveAndFlush(new ErrorRecord(
                defaultProfile(),
                "Blog Draft 에러 기록",
                "BlogDraftException",
                "초안 생성 화면 테스트 중 발생",
                "sourceType 선택값 확인 필요",
                "템플릿 기반으로 안전하게 처리",
                "테스트 메모",
                ErrorStatus.RESOLVED,
                ErrorSeverity.MEDIUM,
                LocalDateTime.of(2099, 5, 29, 10, 0),
                subCategory,
                null
        ));
        errorRecordIds.add(errorRecord.getId());
        return errorRecord;
    }

    private GrowthLog saveGrowthLog() {
        GrowthSubCategory subCategory = saveSubCategory();
        Quest quest = questRepository.saveAndFlush(new Quest(
                defaultProfile(),
                subCategory,
                "Blog Draft Quest " + uniqueSuffix(),
                "Blog Draft Controller 테스트용 Quest",
                Difficulty.NORMAL
        ));
        questIds.add(quest.getId());
        GrowthLog growthLog = growthLogRepository.saveAndFlush(new GrowthLog(
                defaultProfile(),
                quest,
                subCategory,
                30,
                "Blog Draft 학습 기록"
        ));
        growthLogIds.add(growthLog.getId());
        return growthLog;
    }

    private GrowthSubCategory saveSubCategory() {
        String suffix = uniqueSuffix();
        GrowthCategory category = categoryRepository.saveAndFlush(new GrowthCategory(
                "TEST_BLOG_" + suffix,
                "테스트 블로그",
                "Blog Draft 테스트용 성장 영역",
                999
        ));
        categoryIds.add(category.getId());
        GrowthSubCategory subCategory = subCategoryRepository.saveAndFlush(new GrowthSubCategory(
                category,
                "TEST_SUB_" + suffix,
                "테스트 블로그 중분류",
                "Blog Draft 테스트용 성장 항목",
                1
        ));
        subCategoryIds.add(subCategory.getId());
        return subCategory;
    }

    private LocalDate nextReviewDate() {
        DeveloperProfile profile = defaultProfile();
        LocalDate date = LocalDate.of(2099, 6, 1)
                .plusDays(Math.abs(UUID.randomUUID().getMostSignificantBits() % 3000));
        while (dailyReviewRepository.existsByProfileAndReviewDate(profile, date)) {
            date = date.plusDays(1);
        }
        return date;
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
