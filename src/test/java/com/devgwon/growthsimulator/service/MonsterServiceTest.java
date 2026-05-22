package com.devgwon.growthsimulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.devgwon.growthsimulator.dto.request.MonsterAttackRequest;
import com.devgwon.growthsimulator.dto.request.MonsterCreateRequest;
import com.devgwon.growthsimulator.dto.response.DashboardMonsterSummary;
import com.devgwon.growthsimulator.dto.response.MonsterAttackResult;
import com.devgwon.growthsimulator.entity.DeveloperProfile;
import com.devgwon.growthsimulator.entity.GrowthCategory;
import com.devgwon.growthsimulator.entity.GrowthSubCategory;
import com.devgwon.growthsimulator.entity.Monster;
import com.devgwon.growthsimulator.entity.MonsterDifficulty;
import com.devgwon.growthsimulator.entity.MonsterStatus;
import com.devgwon.growthsimulator.entity.MonsterType;
import com.devgwon.growthsimulator.repository.GrowthCategoryRepository;
import com.devgwon.growthsimulator.repository.GrowthSubCategoryRepository;
import com.devgwon.growthsimulator.repository.MonsterRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonsterServiceTest {
    @Mock
    private MonsterRepository monsterRepository;

    @Mock
    private DeveloperProfileService profileService;

    @Mock
    private GrowthCategoryRepository categoryRepository;

    @Mock
    private GrowthSubCategoryRepository subCategoryRepository;

    @InjectMocks
    private MonsterService monsterService;

    @Test
    void createSavesMonsterWithCurrentHpSameAsMaxHp() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        GrowthCategory category = new GrowthCategory("BACKEND", "백엔드", "desc", 1);
        GrowthSubCategory subCategory = new GrowthSubCategory(category, "API", "API 설계", "desc", 1);
        MonsterCreateRequest request = new MonsterCreateRequest();
        request.setName(" 레거시 코드 늪 ");
        request.setCode(" legacy_swamp ");
        request.setDescription("오래된 구조");
        request.setMonsterType(MonsterType.LEGACY_CODE);
        request.setDifficulty(MonsterDifficulty.NORMAL);
        request.setMaxHp(60);
        request.setRewardExp(0);
        request.setRelatedGrowthCategoryId(1L);
        request.setRelatedGrowthSubCategoryId(2L);
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(monsterRepository.existsByProfileAndCode(profile, "LEGACY_SWAMP")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(subCategoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));
        monsterService.create(request);
        ArgumentCaptor<Monster> captor = ArgumentCaptor.forClass(Monster.class);
        verify(monsterRepository).save(captor.capture());
        Monster saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("레거시 코드 늪");
        assertThat(saved.getCode()).isEqualTo("LEGACY_SWAMP");
        assertThat(saved.getCurrentHp()).isEqualTo(60);
        assertThat(saved.getMaxHp()).isEqualTo(60);
        assertThat(saved.getStatus()).isEqualTo(MonsterStatus.ACTIVE);
        assertThat(saved.getRelatedGrowthSubCategory()).isSameAs(subCategory);
    }

    @Test
    void createRejectsDuplicateCode() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        MonsterCreateRequest request = new MonsterCreateRequest();
        request.setName("중복 몬스터");
        request.setCode("DUPLICATED");
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(monsterRepository.existsByProfileAndCode(profile, "DUPLICATED")).thenReturn(true);
        assertThatThrownBy(() -> monsterService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용");
        verify(monsterRepository, never()).save(any());
    }

    @Test
    void attackWeakensAndDefeatsMonster() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        Monster monster = new Monster(
                profile,
                "버그 몬스터",
                "BUG_1",
                "desc",
                MonsterType.BUG,
                MonsterDifficulty.NORMAL,
                60,
                0,
                null,
                null
        );
        when(monsterRepository.findById(1L)).thenReturn(Optional.of(monster));
        MonsterAttackRequest weakenRequest = new MonsterAttackRequest();
        weakenRequest.setDamage(45);
        MonsterAttackResult weakenResult = monsterService.attack(1L, weakenRequest);
        assertThat(weakenResult.currentHp()).isEqualTo(15);
        assertThat(monster.getStatus()).isEqualTo(MonsterStatus.WEAKENED);
        MonsterAttackRequest defeatRequest = new MonsterAttackRequest();
        defeatRequest.setDamage(20);
        MonsterAttackResult defeatResult = monsterService.attack(1L, defeatRequest);
        assertThat(defeatResult.defeated()).isTrue();
        assertThat(monster.getCurrentHp()).isZero();
        assertThat(monster.getStatus()).isEqualTo(MonsterStatus.DEFEATED);
        assertThat(monster.getDefeatedAt()).isNotNull();
    }

    @Test
    void attackRejectsArchivedMonster() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        Monster monster = new Monster(
                profile,
                "보관 몬스터",
                "ARCHIVED",
                "desc",
                MonsterType.ETC,
                MonsterDifficulty.EASY,
                30,
                0,
                null,
                null
        );
        monster.archive();
        when(monsterRepository.findById(1L)).thenReturn(Optional.of(monster));
        assertThatThrownBy(() -> monsterService.attack(1L, new MonsterAttackRequest()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("공격할 수 없습니다");
    }

    @Test
    void getDashboardSummaryReturnsActiveAndRecentDefeated() {
        DeveloperProfile profile = new DeveloperProfile("devgwon");
        Monster active = new Monster(profile, "불안", "ANXIETY", "", MonsterType.CAREER_ANXIETY,
                MonsterDifficulty.NORMAL, 60, 0, null, null);
        Monster defeated = new Monster(profile, "버그", "BUG", "", MonsterType.BUG,
                MonsterDifficulty.EASY, 30, 0, null, null);
        defeated.attack(30);
        when(profileService.getOrCreateDefaultProfile()).thenReturn(profile);
        when(monsterRepository.countByProfileAndStatusIn(
                profile,
                List.of(MonsterStatus.ACTIVE, MonsterStatus.WEAKENED)
        )).thenReturn(1L);
        when(monsterRepository.findFirstByProfileAndStatusInOrderByCurrentHpAsc(
                profile,
                List.of(MonsterStatus.ACTIVE, MonsterStatus.WEAKENED)
        )).thenReturn(Optional.of(active));
        when(monsterRepository.findFirstByProfileAndStatusOrderByDefeatedAtDesc(profile, MonsterStatus.DEFEATED))
                .thenReturn(Optional.of(defeated));
        when(monsterRepository.findTop3ByProfileAndStatusInOrderByCurrentHpAsc(
                profile,
                List.of(MonsterStatus.ACTIVE, MonsterStatus.WEAKENED)
        )).thenReturn(List.of(active));
        DashboardMonsterSummary summary = monsterService.getDashboardSummary();
        assertThat(summary.activeMonsterCount()).isEqualTo(1);
        assertThat(summary.almostDefeatedMonster().name()).isEqualTo("불안");
        assertThat(summary.recentDefeatedMonster().name()).isEqualTo("버그");
        assertThat(summary.activeMonsters()).hasSize(1);
    }
}
