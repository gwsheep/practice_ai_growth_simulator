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

import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterDifficulty;
import com.devgwon.growthsimulator.entity.MonsterStatus;
import com.devgwon.growthsimulator.entity.MonsterType;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthLogRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.MonsterRepository;
import com.devgwon.growthsimulator.service.DeveloperProfileService;
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
class MonsterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperProfileService profileService;

    @Autowired
    private MonsterRepository monsterRepository;

    @Autowired
    private GrowthCategoryRepository categoryRepository;

    @Autowired
    private GrowthSubCategoryRepository subCategoryRepository;

    @Autowired
    private GrowthLogRepository growthLogRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final List<Long> monsterIds = new ArrayList<>();
    private final List<Long> subCategoryIds = new ArrayList<>();
    private final List<Long> categoryIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        deleteByIds("delete from monster where id in (:ids)", monsterIds);
        deleteByIds("delete from monster where related_growth_sub_category_id in (:ids)", subCategoryIds);
        deleteByIds("delete from monster where related_growth_category_id in (:ids)", categoryIds);
        deleteByIds("delete from growth_sub_category where id in (:ids)", subCategoryIds);
        deleteByIds("delete from growth_category where id in (:ids)", categoryIds);
    }

    @Test
    void monstersShowsListModel() throws Exception {
        mockMvc.perform(get("/monsters"))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/list"))
                .andExpect(model().attributeExists("monsters"));
    }

    @Test
    void newMonsterShowsFormModel() throws Exception {
        mockMvc.perform(get("/monsters/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/form"))
                .andExpect(model().attributeExists("monsterForm"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attributeExists("monsterTypes"))
                .andExpect(model().attributeExists("monsterDifficulties"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"));
    }

    @Test
    void createMonsterBindsFormSavesMonsterAndRedirectsToDetail() throws Exception {
        GrowthSubCategory subCategory = saveSubCategory();
        String code = uniqueCode();
        int beforeExp = defaultProfile().getExp();
        long beforeGrowthLogCount = growthLogRepository.count();

        mockMvc.perform(post("/monsters")
                        .param("name", " 레거시 코드 늪 ")
                        .param("code", " " + code.toLowerCase() + " ")
                        .param("description", " 오래된 구조 ")
                        .param("monsterType", MonsterType.LEGACY_CODE.name())
                        .param("difficulty", MonsterDifficulty.NORMAL.name())
                        .param("maxHp", "60")
                        .param("rewardExp", "15")
                        .param("relatedGrowthCategoryId", subCategory.getCategory().getId().toString())
                        .param("relatedGrowthSubCategoryId", subCategory.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/monsters/*"))
                .andExpect(flash().attribute("noticeMessage", "새 몬스터가 등장했습니다."));

        Monster saved = findMonsterByCode(code);
        monsterIds.add(saved.getId());
        assertThat(saved.getName()).isEqualTo("레거시 코드 늪");
        assertThat(saved.getCode()).isEqualTo(code);
        assertThat(saved.getDescription()).isEqualTo("오래된 구조");
        assertThat(saved.getMonsterType()).isEqualTo(MonsterType.LEGACY_CODE);
        assertThat(saved.getDifficulty()).isEqualTo(MonsterDifficulty.NORMAL);
        assertThat(saved.getMaxHp()).isEqualTo(60);
        assertThat(saved.getCurrentHp()).isEqualTo(60);
        assertThat(saved.getRewardExp()).isEqualTo(15);
        assertThat(saved.getStatus()).isEqualTo(MonsterStatus.ACTIVE);
        assertThat(saved.getRelatedGrowthCategory().getId()).isEqualTo(subCategory.getCategory().getId());
        assertThat(saved.getRelatedGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
        assertThat(defaultProfile().getExp()).isEqualTo(beforeExp);
        assertThat(growthLogRepository.count()).isEqualTo(beforeGrowthLogCount);
    }

    @Test
    void createMonsterReturnsFormWhenValidationFails() throws Exception {
        String code = uniqueCode();

        mockMvc.perform(post("/monsters")
                        .param("name", " ")
                        .param("code", code)
                        .param("description", "")
                        .param("monsterType", MonsterType.ETC.name())
                        .param("difficulty", MonsterDifficulty.NORMAL.name())
                        .param("maxHp", "60")
                        .param("rewardExp", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/form"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attributeHasErrors("monsterForm"))
                .andExpect(model().attributeExists("monsterTypes"))
                .andExpect(model().attributeExists("monsterDifficulties"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.monsterForm",
                        org.hamcrest.Matchers.hasProperty("globalError",
                                org.hamcrest.Matchers.hasProperty("defaultMessage", containsString("몬스터 이름")))));

        assertThat(findOptionalMonsterByCode(code)).isEmpty();
    }

    @Test
    void monsterDetailShowsMonsterAndAttackFormModel() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);

        mockMvc.perform(get("/monsters/{id}", monster.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/detail"))
                .andExpect(model().attributeExists("monster"))
                .andExpect(model().attributeExists("attackForm"));
    }

    @Test
    void editMonsterShowsFormModel() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);

        mockMvc.perform(get("/monsters/{id}/edit", monster.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/form"))
                .andExpect(model().attributeExists("monster"))
                .andExpect(model().attributeExists("monsterForm"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attributeExists("monsterTypes"))
                .andExpect(model().attributeExists("monsterDifficulties"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"));
    }

    @Test
    void updateMonsterBindsFormUpdatesMonsterAndRedirectsToDetail() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);
        GrowthSubCategory subCategory = saveSubCategory();
        String updatedCode = uniqueCode();

        mockMvc.perform(post("/monsters/{id}/edit", monster.getId())
                        .param("name", " 버그 몬스터 ")
                        .param("code", " " + updatedCode.toLowerCase() + " ")
                        .param("description", " 수정된 설명 ")
                        .param("monsterType", MonsterType.BUG.name())
                        .param("difficulty", MonsterDifficulty.HARD.name())
                        .param("maxHp", "40")
                        .param("rewardExp", "20")
                        .param("relatedGrowthCategoryId", subCategory.getCategory().getId().toString())
                        .param("relatedGrowthSubCategoryId", subCategory.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monsters/" + monster.getId()))
                .andExpect(flash().attribute("noticeMessage", "몬스터 정보가 수정되었습니다."));

        Monster updated = monsterRepository.findById(monster.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("버그 몬스터");
        assertThat(updated.getCode()).isEqualTo(updatedCode);
        assertThat(updated.getDescription()).isEqualTo("수정된 설명");
        assertThat(updated.getMonsterType()).isEqualTo(MonsterType.BUG);
        assertThat(updated.getDifficulty()).isEqualTo(MonsterDifficulty.HARD);
        assertThat(updated.getMaxHp()).isEqualTo(40);
        assertThat(updated.getCurrentHp()).isEqualTo(40);
        assertThat(updated.getRewardExp()).isEqualTo(20);
        assertThat(updated.getRelatedGrowthCategory().getId()).isEqualTo(subCategory.getCategory().getId());
        assertThat(updated.getRelatedGrowthSubCategory().getId()).isEqualTo(subCategory.getId());
    }

    @Test
    void updateMonsterReturnsFormWhenValidationFails() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);

        mockMvc.perform(post("/monsters/{id}/edit", monster.getId())
                        .param("name", "수정 실패 몬스터")
                        .param("code", monster.getCode())
                        .param("description", "실패")
                        .param("monsterType", MonsterType.ETC.name())
                        .param("difficulty", MonsterDifficulty.NORMAL.name())
                        .param("maxHp", "0")
                        .param("rewardExp", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("monsters/form"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attributeExists("monster"))
                .andExpect(model().attributeHasErrors("monsterForm"))
                .andExpect(model().attributeExists("monsterTypes"))
                .andExpect(model().attributeExists("monsterDifficulties"))
                .andExpect(model().attributeExists("growthCategories"))
                .andExpect(model().attributeExists("growthSubCategories"))
                .andExpect(model().errorCount(1))
                .andExpect(model().attribute("org.springframework.validation.BindingResult.monsterForm",
                        org.hamcrest.Matchers.hasProperty("globalError",
                                org.hamcrest.Matchers.hasProperty("defaultMessage", containsString("최대 HP")))));

        Monster unchanged = monsterRepository.findById(monster.getId()).orElseThrow();
        assertThat(unchanged.getName()).isEqualTo(monster.getName());
        assertThat(unchanged.getMaxHp()).isEqualTo(60);
        assertThat(unchanged.getCurrentHp()).isEqualTo(60);
    }

    @Test
    void attackMonsterReducesHpAndRedirectsToDetail() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);

        mockMvc.perform(post("/monsters/{id}/attack", monster.getId())
                        .param("damage", "20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monsters/" + monster.getId()))
                .andExpect(flash().attribute("noticeMessage", monster.getName() + " 몬스터에게 20 데미지를 줬어요."));

        Monster attacked = monsterRepository.findById(monster.getId()).orElseThrow();
        assertThat(attacked.getCurrentHp()).isEqualTo(40);
        assertThat(attacked.getStatus()).isEqualTo(MonsterStatus.ACTIVE);
    }

    @Test
    void attackMonsterCanDefeatMonsterWithoutGrantingGrowthExp() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 30, 50);
        int beforeExp = defaultProfile().getExp();
        long beforeGrowthLogCount = growthLogRepository.count();

        mockMvc.perform(post("/monsters/{id}/attack", monster.getId())
                        .param("damage", "30"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monsters/" + monster.getId()))
                .andExpect(flash().attribute("noticeMessage",
                        monster.getName() + " 몬스터를 처치했어요. 보상 EXP는 아직 표시용으로만 남겨둘게요."));

        Monster defeated = monsterRepository.findById(monster.getId()).orElseThrow();
        assertThat(defeated.getCurrentHp()).isZero();
        assertThat(defeated.getStatus()).isEqualTo(MonsterStatus.DEFEATED);
        assertThat(defeated.getDefeatedAt()).isNotNull();
        assertThat(defaultProfile().getExp()).isEqualTo(beforeExp);
        assertThat(growthLogRepository.count()).isEqualTo(beforeGrowthLogCount);
    }

    @Test
    void archiveMonsterMovesMonsterOutOfList() throws Exception {
        Monster monster = saveMonster(uniqueCode(), 60);

        mockMvc.perform(post("/monsters/{id}/archive", monster.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/monsters"))
                .andExpect(flash().attribute("noticeMessage", "몬스터를 보관함으로 이동했습니다."));

        Monster archived = monsterRepository.findById(monster.getId()).orElseThrow();
        assertThat(archived.getStatus()).isEqualTo(MonsterStatus.ARCHIVED);
        assertThat(monsterRepository.findByProfileAndStatusNotOrderByCreatedAtDesc(defaultProfile(), MonsterStatus.ARCHIVED))
                .extracting(Monster::getId)
                .doesNotContain(monster.getId());
    }

    private Monster saveMonster(String code, int maxHp) {
        return saveMonster(code, maxHp, 0);
    }

    private Monster saveMonster(String code, int maxHp, int rewardExp) {
        Monster monster = monsterRepository.saveAndFlush(new Monster(
                defaultProfile(),
                "테스트 몬스터 " + code,
                code,
                "Monster Controller 테스트용",
                MonsterType.ETC,
                MonsterDifficulty.NORMAL,
                maxHp,
                rewardExp,
                null,
                null
        ));
        monsterIds.add(monster.getId());
        return monster;
    }

    private GrowthSubCategory saveSubCategory() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        GrowthCategory category = categoryRepository.saveAndFlush(new GrowthCategory(
                "TEST_MON_" + suffix,
                "테스트 몬스터",
                "Monster Controller 테스트용 성장 영역",
                999
        ));
        categoryIds.add(category.getId());
        GrowthSubCategory subCategory = subCategoryRepository.saveAndFlush(new GrowthSubCategory(
                category,
                "TEST_SUB_" + suffix,
                "테스트 중분류",
                "Monster Controller 테스트용 성장 항목",
                1
        ));
        subCategoryIds.add(subCategory.getId());
        return subCategory;
    }

    private String uniqueCode() {
        return "TEST_MON_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private Monster findMonsterByCode(String code) {
        return findOptionalMonsterByCode(code).orElseThrow();
    }

    private java.util.Optional<Monster> findOptionalMonsterByCode(String code) {
        return monsterRepository.findByProfileOrderByCreatedAtDesc(defaultProfile()).stream()
                .filter(monster -> monster.getCode().equals(code))
                .findFirst();
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
