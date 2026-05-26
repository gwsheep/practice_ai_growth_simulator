package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

import com.devgwon.growthsimulator.dto.response.QuestCompleteResult;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.Difficulty;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthLog;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Quest;
import com.devgwon.growthsimulator.entity.QuestStatus;
import com.devgwon.growthsimulator.exception.QuestAlreadyCompletedException;
import com.devgwon.growthsimulator.exception.QuestNotFoundException;
import com.devgwon.growthsimulator.repository.DeveloperProfileRepository;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.QuestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootTest
class QuestServiceTransactionTest {
    @Autowired
    private QuestService questService;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private DeveloperProfileRepository profileRepository;

    @Autowired
    private GrowthCategoryRepository categoryRepository;

    @Autowired
    private GrowthSubCategoryRepository subCategoryRepository;

    @Autowired
    private GrowthLogRepository growthLogRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @SpyBean
    private CharacterMessageService characterMessageService;

    private final List<Long> questIds = new ArrayList<>();
    private final List<Long> profileIds = new ArrayList<>();
    private final List<Long> subCategoryIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        deleteByIds("delete from growth_log where quest_id in (:ids)", questIds);
        deleteByIds("delete from quest where id in (:ids)", questIds);
        deleteByIds("delete from growth_sub_category where id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_category where id in (:ids)", categoryIds);
        deleteByIds("delete from developer_profile where id in (:ids)", profileIds);
    }

    @Test
    void completeQuestUpdatesQuestProfileSubCategoryAndGrowthLogInOneTransaction() {
        Quest quest = saveQuest(Difficulty.NORMAL, 90, 90);

        QuestCompleteResult result = questService.completeQuest(quest.getId());

        Quest completedQuest = questRepository.findById(quest.getId()).orElseThrow();
        DeveloperProfile profile = profileRepository.findById(completedQuest.getProfile().getId()).orElseThrow();
        GrowthSubCategory subCategory = subCategoryRepository.findById(quest.getSubCategory().getId()).orElseThrow();
        List<GrowthLog> logs = growthLogRepository.findAll();

        assertThat(completedQuest.getStatus()).isEqualTo(QuestStatus.COMPLETED);
        assertThat(completedQuest.getCompletedAt()).isNotNull();
        assertThat(profile.getLevel()).isEqualTo(2);
        assertThat(profile.getExp()).isEqualTo(20);
        assertThat(subCategory.getTotalExp()).isEqualTo(120);
        assertThat(subCategory.getLevel()).isEqualTo(2);
        assertThat(logs)
                .filteredOn(log -> log.getQuest().getId().equals(quest.getId()))
                .singleElement()
                .satisfies(log -> {
                    assertThat(log.getExpGained()).isEqualTo(30);
                    assertThat(log.getMessage()).isNotBlank();
                });
        assertThat(result.getQuestId()).isEqualTo(quest.getId());
        assertThat(result.getExpReward()).isEqualTo(30);
        assertThat(result.getCurrentLevel()).isEqualTo(2);
        assertThat(result.getCurrentExp()).isEqualTo(20);
        assertThat(result.isLevelUp()).isTrue();
    }

    @Test
    void completeQuestRollsBackQuestProfileAndSubCategoryWhenExceptionOccurs() {
        Quest quest = saveQuest(Difficulty.EASY, 0, 0);
        doThrow(new RuntimeException("메시지 생성 실패"))
                .when(characterMessageService)
                .completeMessage(any(GrowthSubCategory.class), eq(false));

        assertThatThrownBy(() -> questService.completeQuest(quest.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("메시지 생성 실패");

        Quest rolledBackQuest = questRepository.findById(quest.getId()).orElseThrow();
        DeveloperProfile profile = profileRepository.findById(rolledBackQuest.getProfile().getId()).orElseThrow();
        GrowthSubCategory subCategory = subCategoryRepository.findById(quest.getSubCategory().getId()).orElseThrow();

        assertThat(rolledBackQuest.getStatus()).isEqualTo(QuestStatus.READY);
        assertThat(rolledBackQuest.getCompletedAt()).isNull();
        assertThat(profile.getLevel()).isEqualTo(1);
        assertThat(profile.getExp()).isZero();
        assertThat(subCategory.getTotalExp()).isZero();
        assertThat(subCategory.getLevel()).isEqualTo(1);
        assertThat(growthLogRepository.findAll())
                .filteredOn(log -> log.getQuest().getId().equals(quest.getId()))
                .isEmpty();
    }

    @Test
    void completeQuestRejectsAlreadyCompletedQuest() {
        Quest quest = saveQuest(Difficulty.EASY, 0, 0);
        quest.complete();
        questRepository.saveAndFlush(quest);

        assertThatThrownBy(() -> questService.completeQuest(quest.getId()))
                .isInstanceOf(QuestAlreadyCompletedException.class)
                .hasMessageContaining("이미 완료");

        DeveloperProfile profile = profileRepository.findById(quest.getProfile().getId()).orElseThrow();
        GrowthSubCategory subCategory = subCategoryRepository.findById(quest.getSubCategory().getId()).orElseThrow();
        assertThat(profile.getExp()).isZero();
        assertThat(subCategory.getTotalExp()).isZero();
        assertThat(growthLogRepository.findAll())
                .filteredOn(log -> log.getQuest().getId().equals(quest.getId()))
                .isEmpty();
    }

    @Test
    void completeQuestRejectsUnknownQuest() {
        assertThatThrownBy(() -> questService.completeQuest(-1L))
                .isInstanceOf(QuestNotFoundException.class)
                .hasMessageContaining("퀘스트를 찾을 수 없습니다");
    }

    private Quest saveQuest(Difficulty difficulty, int profileExp, int subCategoryExp) {
        DeveloperProfile profile = new DeveloperProfile("test-" + UUID.randomUUID());
        profile.addExp(profileExp);
        profile = profileRepository.saveAndFlush(profile);
        profileIds.add(profile.getId());

        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        GrowthCategory category = categoryRepository.saveAndFlush(new GrowthCategory(
                "TEST_QT_" + suffix,
                "테스트 성장",
                "Quest 완료 트랜잭션 테스트용 성장 영역",
                999
        ));
        categoryIds.add(category.getId());

        GrowthSubCategory subCategory = new GrowthSubCategory(
                category,
                "TEST_SUB_" + suffix,
                "테스트 중분류",
                "Quest 완료 트랜잭션 테스트용 성장 항목",
                1
        );
        subCategory.addExp(subCategoryExp);
        subCategory = subCategoryRepository.saveAndFlush(subCategory);
        subCategoryIds.add(subCategory.getId());

        Quest quest = questRepository.saveAndFlush(new Quest(
                profile,
                subCategory,
                "트랜잭션 테스트 Quest",
                "Quest 완료 트랜잭션 테스트",
                difficulty
        ));
        questIds.add(quest.getId());
        return quest;
    }

    private void deleteByIds(String sql, List<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(sql, Map.of("ids", ids));
    }
}
