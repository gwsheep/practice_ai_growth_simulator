package com.devgwon.growthsimulator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.devgwon.growthsimulator.entity.DailyReview;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.repository.DailyReviewRepository;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.service.DeveloperProfileService;
import java.time.LocalDate;
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
class DailyReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperProfileService profileService;

    @Autowired
    private DailyReviewRepository dailyReviewRepository;

    @Autowired
    private GrowthCategoryRepository categoryRepository;

    @Autowired
    private GrowthSubCategoryRepository subCategoryRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final List<Long> reviewIds = new ArrayList<>();
    private final List<Long> subCategoryIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        deleteByIds("delete from daily_review where id in (:ids)", reviewIds);
        deleteByIds("delete from daily_review where related_sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_sub_category where id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_category where id in (:ids)", categoryIds);
    }

    @Test
    void reviewsShowsListModel() throws Exception {
        mockMvc.perform(get("/daily-reviews"))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/list"))
                .andExpect(model().attributeExists("reviews"));
    }

    @Test
    void editReviewShowsFormModelForExistingReview() throws Exception {
        DailyReview review = saveReview(nextAvailableDate());

        mockMvc.perform(get("/daily-reviews/{id}/edit", review.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/form"))
                .andExpect(model().attributeExists("review"))
                .andExpect(model().attributeExists("reviewForm"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("moodScores"));
    }

    @Test
    void createReviewBindsFormSavesReviewAndRedirectsToDetail() throws Exception {
        LocalDate reviewDate = nextAvailableDate();
        GrowthSubCategory subCategory = saveSubCategory();

        mockMvc.perform(post("/daily-reviews")
                        .param("reviewDate", reviewDate.toString())
                        .param("learnedText", " JPA 변경 감지 테스트 ")
                        .param("difficultyText", "")
                        .param("tomorrowPlanText", " MockMvc 검증 ")
                        .param("moodScore", "4")
                        .param("relatedGrowthSubCategoryId", subCategory.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/daily-reviews/*"))
                .andExpect(flash().attribute("noticeMessage", "오늘의 회고가 저장되었습니다."));

        DailyReview saved = dailyReviewRepository.findByProfileAndReviewDate(defaultProfile(), reviewDate)
                .orElseThrow();
        reviewIds.add(saved.getId());
        assertThat(saved.getLearnedText()).isEqualTo("JPA 변경 감지 테스트");
        assertThat(saved.getDifficultyText()).isEmpty();
        assertThat(saved.getTomorrowPlanText()).isEqualTo("MockMvc 검증");
        assertThat(saved.getMoodScore()).isEqualTo(4);
        assertThat(saved.getRelatedGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
    }

    @Test
    void createReviewReturnsFormWhenValidationFails() throws Exception {
        LocalDate reviewDate = nextAvailableDate();

        mockMvc.perform(post("/daily-reviews")
                        .param("reviewDate", reviewDate.toString())
                        .param("learnedText", " ")
                        .param("difficultyText", " ")
                        .param("tomorrowPlanText", " ")
                        .param("moodScore", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/form"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attributeHasErrors("reviewForm"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("moodScores"))
                .andExpect(model().errorCount(1));

        assertThat(dailyReviewRepository.findByProfileAndReviewDate(defaultProfile(), reviewDate)).isEmpty();
    }

    @Test
    void reviewDetailShowsReviewModel() throws Exception {
        DailyReview review = saveReview(nextAvailableDate());

        mockMvc.perform(get("/daily-reviews/{id}", review.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/detail"))
                .andExpect(model().attributeExists("review"));
    }

    @Test
    void updateReviewBindsFormUpdatesReviewAndRedirectsToDetail() throws Exception {
        DailyReview review = saveReview(nextAvailableDate());
        GrowthSubCategory subCategory = saveSubCategory();
        LocalDate updatedDate = nextAvailableDate();

        mockMvc.perform(post("/daily-reviews/{id}/edit", review.getId())
                        .param("reviewDate", updatedDate.toString())
                        .param("learnedText", " Controller 테스트 수정 ")
                        .param("difficultyText", " 바인딩 확인 ")
                        .param("tomorrowPlanText", " 전체 테스트 실행 ")
                        .param("moodScore", "5")
                        .param("relatedGrowthSubCategoryId", subCategory.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/daily-reviews/" + review.getId()))
                .andExpect(flash().attribute("noticeMessage", "회고가 수정되었습니다."));

        DailyReview updated = dailyReviewRepository.findById(review.getId()).orElseThrow();
        assertThat(updated.getReviewDate()).isEqualTo(updatedDate);
        assertThat(updated.getLearnedText()).isEqualTo("Controller 테스트 수정");
        assertThat(updated.getDifficultyText()).isEqualTo("바인딩 확인");
        assertThat(updated.getTomorrowPlanText()).isEqualTo("전체 테스트 실행");
        assertThat(updated.getMoodScore()).isEqualTo(5);
        assertThat(updated.getRelatedGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
    }

    @Test
    void updateReviewReturnsFormWhenValidationFails() throws Exception {
        DailyReview review = saveReview(nextAvailableDate());

        mockMvc.perform(post("/daily-reviews/{id}/edit", review.getId())
                        .param("reviewDate", review.getReviewDate().toString())
                        .param("learnedText", " ")
                        .param("difficultyText", " ")
                        .param("tomorrowPlanText", " ")
                        .param("moodScore", "3"))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/form"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attributeExists("review"))
                .andExpect(model().attributeHasErrors("reviewForm"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().attributeExists("moodScores"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasErrors("reviewForm"));

        DailyReview unchanged = dailyReviewRepository.findById(review.getId()).orElseThrow();
        assertThat(unchanged.getLearnedText()).isEqualTo("초기 학습");
        assertThat(unchanged.getDifficultyText()).isEqualTo("초기 어려움");
        assertThat(unchanged.getTomorrowPlanText()).isEqualTo("초기 계획");
    }

    @Test
    void deleteReviewRemovesReviewAndRedirectsToList() throws Exception {
        DailyReview review = saveReview(nextAvailableDate());

        mockMvc.perform(post("/daily-reviews/{id}/delete", review.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/daily-reviews"))
                .andExpect(flash().attribute("noticeMessage", "회고가 삭제되었습니다."));

        assertThat(dailyReviewRepository.findById(review.getId())).isEmpty();
        reviewIds.remove(review.getId());
    }

    @Test
    void createReviewReturnsFormWhenMoodScoreIsOutOfRange() throws Exception {
        LocalDate reviewDate = nextAvailableDate();

        mockMvc.perform(post("/daily-reviews")
                        .param("reviewDate", reviewDate.toString())
                        .param("learnedText", "기분 점수 검증")
                        .param("difficultyText", "")
                        .param("tomorrowPlanText", "")
                        .param("moodScore", "6"))
                .andExpect(status().isOk())
                .andExpect(view().name("daily-reviews/form"))
                .andExpect(model().attributeHasErrors("reviewForm"))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.reviewForm",
                        org.hamcrest.Matchers.hasProperty("globalError",
                                org.hamcrest.Matchers.hasProperty("defaultMessage", containsString("기분 점수")))));
    }

    private DailyReview saveReview(LocalDate reviewDate) {
        DailyReview review = dailyReviewRepository.saveAndFlush(new DailyReview(
                defaultProfile(),
                reviewDate,
                "초기 학습",
                "초기 어려움",
                "초기 계획",
                3,
                saveSubCategory()
        ));
        reviewIds.add(review.getId());
        return review;
    }

    private GrowthSubCategory saveSubCategory() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        GrowthCategory category = categoryRepository.saveAndFlush(new GrowthCategory(
                "TEST_DR_" + suffix,
                "테스트 회고",
                "Daily Review Controller 테스트용 성장 영역",
                999
        ));
        categoryIds.add(category.getId());
        GrowthSubCategory subCategory = subCategoryRepository.saveAndFlush(new GrowthSubCategory(
                category,
                "TEST_SUB_" + suffix,
                "테스트 중분류",
                "Daily Review Controller 테스트용 성장 항목",
                1
        ));
        subCategoryIds.add(subCategory.getId());
        return subCategory;
    }

    private LocalDate nextAvailableDate() {
        DeveloperProfile profile = defaultProfile();
        LocalDate date = LocalDate.of(2099, 1, 1)
                .plusDays(Math.abs(UUID.randomUUID().getMostSignificantBits() % 3000));
        while (dailyReviewRepository.existsByProfileAndReviewDate(profile, date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    private DeveloperProfile defaultProfile() {
        return profileService.getOrCreateDefaultProfile();
    }

    private void deleteByIds(String sql, List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(sql, Map.of("ids", ids));
    }
}
